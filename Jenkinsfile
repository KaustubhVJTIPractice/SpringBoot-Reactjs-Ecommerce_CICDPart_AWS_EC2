pipeline {
    agent any

    environment {
        AWS_REGION = "ap-south-1"
        CLUSTER_NAME = "ecommerce-eks"
        NAMESPACE = "ecommerce"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Configure kubectl') {
            steps {
                sh """
                aws eks update-kubeconfig \
                  --name $CLUSTER_NAME \
                  --region $AWS_REGION
                """
            }
        }

        stage('Apply Secrets') {
            steps {
                echo "Applying Kubernetes secrets"
                sh """
                kubectl apply -f k8s/rds-secret.yaml -n $NAMESPACE
                """
            }
        }

        stage('Deploy Backend') {
            steps {
                echo "Deploying Backend"
                sh """
                kubectl apply -f k8s/backend-deployment.yaml -n $NAMESPACE
                """
            }
        }

        stage('Deploy Frontend') {
            steps {
                echo "Deploying Frontend"
                sh """
                kubectl apply -f k8s/frontend-deployment.yaml -n $NAMESPACE
                """
            }
        }

        stage('Verify Deployment') {
            steps {
                sh """
                kubectl rollout status deployment/backend-deployment -n $NAMESPACE
                kubectl rollout status deployment/frontend-deployment -n $NAMESPACE
                kubectl get pods -n $NAMESPACE
                """
            }
        }
    }

    post {
        failure {
            echo "Deployment failed! Check logs in Jenkins and Kubernetes pods."
        }
        success {
            echo "Deployment succeeded!"
        }
    }
}
