import axios from "axios";

const api = axios.create({
  baseURL: "http://k8s-ecommerc-ecommerc-beadc8ac6d-1700331625.ap-south-1.elb.amazonaws.com"
});

export default API;

