<script lang="ts">
    import Imgcontainer from "./Imgcontainer.svelte";
    import { createEventDispatcher } from "svelte";
    import { mdiDownload } from "@mdi/js";
    import { Icon } from "@smui/icon-button";
    import IconButton from "@smui/icon-button";

    export let videoInfo;

    const date = new Date(videoInfo.published * 1000); // Convert seconds to milliseconds
    let publish_ = date.toLocaleDateString();

    let dispatch = createEventDispatcher();

    

    
</script>

<Imgcontainer img={videoInfo.thumbnails[4].url} length={videoInfo.length} />
<h1>{videoInfo.title}</h1>

<p>{publish_}</p>
<div style="display: flex; align-items: center;">
    <!-- <Button on:click={() => menu.setOpen(true)} ripple={false}>
        <Label>video</Label>
        <Icon tag="svg" viewBox="0 0 24 24">
            <path fill="currentColor" d={mdiDownload} />
        </Icon>
    </Button> -->
    <!-- <Button on:click={() => menu.setOpen(true)} ripple={false}>
        <Label>audio</Label>
    </Button> -->
    
</div>

<!-- <div class ="close-button-container">
        <Button on:click={_ =>{dispatch("closeEvent",true)}}><Icon name="x"/></Button>
    </div> -->
<style>
    .videodetail-container {
        position: relative;
        margin: 10px;
        overflow: hidden;
    }
    .videodetail-download-container {
        height: 50px;
        display: flex;
        flex-direction: row-reverse;
    }
    .close-button-container {
        position: absolute;
        right: 0;
        top: 0;
    }
</style>
