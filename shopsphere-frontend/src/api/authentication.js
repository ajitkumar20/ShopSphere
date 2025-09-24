import axios from "axios";
import { API_BASE_URL } from "./constant";

export const loginAPI = async (body)=>{
    const url = API_BASE_URL + '/api/auth/login';
    try{
        const response = await axios(url,{
            method:"POST",
            data:body
        });
        return response?.data;
    }
    catch(err){
        throw new Error(err);
    }
}