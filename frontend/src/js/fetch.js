import axios from "axios";
import qs from "qs";

export async function getSecure() {
  let res = await fetch('/secure', { credentials: 'same-origin' });
  let secureResponse = await res.json();
  return JSON.stringify(secureResponse.session);
}

export async function getApi(api_token) {
  let res = await fetch('/api', {
    headers: {
      'Authorization': 'Bearer ' + api_token,
      Accept: "application/json",
    },
  });
  return await res.json();
}

export async function infosByTag(tagName, params, onSuccess) {
  await axios
    .get(
      "http://127.0.0.1:9090/v1.0/youtube/tag/" +
      tagName +
      "/videos",
      {
        headers: {},
        params: params,
        paramsSerializer: function (params) {
          return qs.stringify(params, {
            arrayFormat: "repeat",
          });
        },
      },
    )
    .then((response) => {console.log(response.data); onSuccess(response.data) })
    // .then(data => {
    //   onSuccess(data)
    // })
    .catch((error) => console.log(error));

  // fetch("http://127.0.0.1:7070/youtube/tag/" + tagName, {
  //   method: 'GET',
  // })
  // .then(res => {
  //       console.log("res:" + res)
  //       return res.json()
  // })
  // .then(data => {
  //   onSuccess(data)
  //   // console.log("data:" + console.log(JSON.stringify(data)))
  //   //return data;//can json be a any type? not sure      
  // })
  // .catch(error => console.log('request error'+ error))
}
