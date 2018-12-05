import axios from 'axios';

export async function generateRandomVideo(name, fps, scale) {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/generate-random-video/' + name + '/' + fps + '/' + scale,
            responseType: 'json'
        })
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function getGIF(name) {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/getGIF/' + name,
            responseType: 'blob'
        });
        return response;
    } catch (error) {
        console.error(error);
    }
}

export async function getVariantsCSV(name) {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/getVariantsCSV',
            responseType: 'blob'
        });
        return response;
    } catch (error) {
        console.error(error);
    }
}

export async function getMP4(name) {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/getMP4/' + name,
            responseType: 'blob'
        });
        return response;
    } catch (error) {
        console.error(error);
    }
}

export async function analyzeDurations() {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/analyzeDurations',
            responseType: 'json'
        });
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function getVariants() {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/getVariants',
            responseType: 'text'
        });
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function getModel() {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/getModel',
            responseType: 'json'
        });
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function getThumbnail(id) {
    try {
        const response = await axios({
            method: 'get',
            url: 'http://localhost:8080/getThumbnail/' + id,
            responseType: 'blob'
        });
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function generateVideo(name, fps, scale, json) {
    var config = {
        headers: {
            "Access-Control-Allow-Origin": "http://localhost:8080",
            "Access-Control-Allow-Methods": "GET, POST",
            "Content-Type": "application/json"
        }
      }
    try {
        console.log('json sent: ' + JSON.stringify(json, null, 4))
        const response = await axios({
            method: 'post',
            url: 'http://localhost:8080/generate-video/' + name + '/' + fps + '/' + scale,
            data: json
        })
        return response.data;
    } catch (error) {
        console.error(error);
    }
}