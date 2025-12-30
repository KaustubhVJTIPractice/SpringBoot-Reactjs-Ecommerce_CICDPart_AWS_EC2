pipeline {
    agent any

    environment {
        AWS_REGION   = "ap-south-1"
        CLUSTER_NAME = "ecommerce-eks"
        NAMESPACE    = "ecommerce"
    }

    stages {

        stage('Checkout Repository') {
            steps {
                checkout scm
            }
        }

        stage('Configure kubectl') {
            steps {
                sh """
                aws eks update-kubeconfig \
                  --region ${AWS_REGION} \
                  --name ${CLUSTER_NAME}
                """
            }
        }

        stage('Deploy Kubernetes Resources') {
            steps {
                sh """
                # Namespace
                kubectl apply -f k8s/namespace.yaml

                # Secrets
                kubectl apply -f k8s/secrets/rds-secret.yaml -n ${NAMESPACE}

                # Backend
                kubectl apply -f k8s/backend/backend-deployment.yaml -n ${NAMESPACE}
                kubectl apply -f k8s/backend/backend-service.yaml -n ${NAMESPACE}

                # Frontend
                kubectl apply -f k8s/frontend/frontend-deployment.yaml -n ${NAMESPACE}
                kubectl apply -f k8s/frontend/frontend-service.yaml -n ${NAMESPACE}

                # Ingress (ALB)
                kubectl apply -f k8s/ingress/ingress.yaml -n ${NAMESPACE}
                """
            }
        }

        stage('Verify Deployment') {
            steps {
                sh """
                kubectl rollout status deployment/backend-deployment -n ${NAMESPACE}
                kubectl rollout status deployment/frontend-deployment -n ${NAMESPACE}

                kubectl get pods -n ${NAMESPACE}
                kubectl get svc -n ${NAMESPACE}
                kubectl get ingress -n ${NAMESPACE}
                """
            }
        }
    }

    post {
        success {
            echo "✅ CD completed successfully – app deployed on EKS (EC2)"
        }
        failure {
            echo "❌ CD failed – check kubectl logs and Jenkins output"
        }
    }
}
