import axios from "axios";

const API = axios.create({
  baseURL: "http://k8s-ecommerc-ecommerc-beadc8ac6d-1700331625.ap-south-1.elb.amazonaws.com/api",
  headers: {
    "Content-Type": "application/json"
  }
});

export default API;
