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
                sh '''
                aws eks update-kubeconfig \
                  --region $AWS_REGION \
                  --name $CLUSTER_NAME
                '''
            }
        }


        stage('Deploy Kubernetes Resources') {
            steps {
                sh '''
                # Ensure namespace exists (Terraform-safe)
                kubectl get ns $NAMESPACE || kubectl create ns $NAMESPACE

                # Apply RDS secrets
                kubectl apply -f k8s/secrets/rds-secret.yaml -n $NAMESPACE

                # Backend deployment
                kubectl apply -f k8s/backend/backend-deployment.yaml -n $NAMESPACE
                kubectl apply -f k8s/backend/backend-service.yaml -n $NAMESPACE

                # Frontend deployment
                kubectl apply -f k8s/frontend/frontend-deployment.yaml -n $NAMESPACE
                kubectl apply -f k8s/frontend/frontend-service.yaml -n $NAMESPACE

                # Force new image pull from ECR
                kubectl rollout restart deployment/backend-deployment -n $NAMESPACE
                kubectl rollout restart deployment/frontend-deployment -n $NAMESPACE

                # Wait for ALB controller (created via Terraform IRSA)
                kubectl wait --for=condition=Available deployment/aws-load-balancer-controller \
                  -n kube-system --timeout=180s

                # Apply Ingress
                kubectl apply -f k8s/ingress/alb-ingress.yaml -n $NAMESPACE
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                kubectl rollout status deployment/backend-deployment -n $NAMESPACE
                kubectl rollout status deployment/frontend-deployment -n $NAMESPACE

                kubectl get pods -n $NAMESPACE
                kubectl get svc -n $NAMESPACE
                kubectl get ingress -n $NAMESPACE
                '''
            }
        }
    }

    post {
        success {
            echo "✅ CD completed successfully – app deployed on EKS"
        }
        failure {
            echo "❌ CD failed – check Jenkins logs & kubectl output"
        }
    }
}
