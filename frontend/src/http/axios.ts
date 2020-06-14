import $axios, { AxiosRequestConfig } from "axios";
import store from "../store";

const axios = $axios.create({
  baseURL: process.env.VUE_APP_BACKEND_BASE_URL
});
export default axios;

export const axiosWrap = function(params: AxiosRequestConfig) {
  return axios(params)
    .then((res: any) => {
      return res.data;
    })
}