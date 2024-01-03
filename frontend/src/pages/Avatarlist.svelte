<script>
    import Videopreview from "./Videopreview.svelte";
    import { infosByTag } from '../js/fetch.js';
    import Videospool from './Videospool.svelte';
    export let data
    let pool = data.videos 
</script>

<div class = "top-channels-container">
    <div class ="avatar-container">
        <img src="https://yt3.googleusercontent.com/mUhuJiCiL8jf0Ngf9sh7BFBZCO0MUL2JyH_5ElHbV2fd13hxZ9zQ3-x-YePA_-PCUUH360G0=s88-c-k-c0x00ffffff-no-rj-mo" alt="">
    </div>
    {#if data.avatars && data.avatars && Array.isArray(data.avatars)}
        {#each data.avatars as avatar}
            <div class="avatar-container" on:click={e => {infosByTag("/youtube/channel/" + avatar.cahnnal_id, res => pool=res.videos)}}>
            </div>
        {/each}
    {/if}
    <div class="showmore-container" >more</div>
    
</div>
<style>
    .top-channels-container{
        position:relative;
        margin-top:10px;
        margin-left:10px;
        margin-right:10px;
        height: 100px;
        width:99%;
        background-color: rgb(249, 249, 249);
		border-bottom-style:solid; 
		border-bottom-width: 2px;
		border-bottom-color: #dddddd;
        border-top-style:solid; 
		border-top-width: 2px;
		border-top-color: #dddddd;
        display: flex;
        align-items: center;
    }
    .avatar-container{
        height:70px;
        width:70px;
        background-color: aqua;
        margin:10px;
        
    }
    .showmore-container{
        border-radius: 3px;
        position: absolute;
        background-color: rgb(0, 0, 0);
        color: aliceblue;
        /*font-size: 20px;
        font-weight:bold; */
        width:45px;
        opacity: 0.4;
        bottom: 5px;
        right: 6px;
        text-align: center
    }
</style>