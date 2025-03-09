
import axios from "@/axios";


export function upload(file){
    let formData = new FormData()
    formData.append("file", file);
    return axios.post('/upload', formData)
}
