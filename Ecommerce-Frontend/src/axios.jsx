import axios from "axios";

const API = axios.create({
  baseURL: "/api",    // ALB routes this to backend
});

export default API;

