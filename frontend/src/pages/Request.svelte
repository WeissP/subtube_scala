<script>
    import { createEventDispatcher } from 'svelte';

    export let reqType
    export let respType
    let link
    //three type can be identified with a 2bit identifercode, after id code should be get information
    if (reqType == "topic"){
        let link = "";
    }else if (reqType == "channel"){
        let link = "";
    }else if (reqType == "link"){
        let link = "";
    }
    function search(){
      fetch("http://127.0.0.1:7070/" + encodeURIComponent(link), {
        method: 'GET',
      })
      .then(res => {
            return res.json()
      })
      .then(data => {
        sendDataToParent(data);//can json be a any type? not sure      
      })
      .catch(error => console.log('request error'))
    }
  
    let dispatch = createEventDispatcher();
    function sendDataToParent(data){
      dispatch(respType, data)
    }
</script>