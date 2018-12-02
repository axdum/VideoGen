import axios from 'axios';

export async function generateRandomVideo(name) {
    try {
        const response = await axios({
            method:'get',
            url: 'http://localhost:8080/generate-video/' + name,
            responseType:'json'
          })
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function getGIF(name){
    try {
        const response = await axios({
            method:'get',
            url: 'http://localhost:8080/getGIF/' + name,
            responseType: 'blob'
          });
        return response;
    } catch (error) {
        console.error(error);
    }
}

export async function getMP4(name){
    try {
        const response = await axios({
            method:'get',
            url: 'http://localhost:8080/getMP4/' + name,
            responseType: 'blob'
          });
          console.log('RESP: ' + JSON.stringify(response, null, 4))
        return response;
    } catch (error) {
        console.error(error);
    }
}