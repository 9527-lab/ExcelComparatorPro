
import axios from "@/axios";


export function compare(data){
    return axios.post('/compare', data)
}
