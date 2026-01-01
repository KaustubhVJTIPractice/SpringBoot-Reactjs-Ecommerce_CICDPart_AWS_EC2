resource "kubernetes_config_map" "aws_auth" {
  metadata {
    name      = "aws-auth"
    namespace = "kube-system"
  }

  data = {
    mapRoles = <<YAML
- rolearn: ${aws_iam_role.worker_role.arn}
  username: system:node:{{EC2PrivateDNSName}}
  groups:
    - system:bootstrappers
    - system:nodes

- rolearn: arn:aws:iam::155288859203:role/Jenkins_EC2_Role
  username: jenkins
  groups:
    - system:masters
YAML
  }

  depends_on = [
    aws_eks_cluster.eks
  ]
}
