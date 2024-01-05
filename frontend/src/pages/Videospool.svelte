<script lang="ts">
    import Videopreview from "./Videopreview.svelte";
    import Videodiaglog from "./Videodiaglog.svelte";

    let pool;
    let videodetail;
    import { onMount } from "svelte";
    import { Icon } from "@smui/icon-button";
    import { mdiClose } from "@mdi/js";

    import LayoutGrid, { Cell } from "@smui/layout-grid";

    import Fab from "@smui/fab";

    import { Button, Modal, ModalBody, ModalFooter } from "yesvelte";
    // let length = pool[0].videos_info.length
    // let publish =pool[0].videos_info.published
    // let title = pool[0].videos_info.title
    // let img = pool[0].videos_info.thumbnails[0].url
    // $:onMount(e =>console.log("pool:" + pool));

    import IconButton from "@smui/icon-button";
    import Videodetail from "./Videodetail.svelte";

    let open = false;
    let selected = "Nothing yet";
    let active = "Information";
</script>

<Videodiaglog {videodetail} bind:open />

<!-- <div class="videopool-container"> -->

<LayoutGrid
    on:videosEvent={(e) => {
        pool = e.detail;
    }}
>
    {#if pool && Array.isArray(pool)}
        {#each pool as video, index}
            <Cell span={3}>
                <Videopreview
                    videoInfo={video.info.media}
                    on:videoEvent={(e) => {
                        videodetail = e.detail;
                        open = true;
                        selected = "Nothing yet";
                    }}
                />
            </Cell>
        {/each}
    {/if}
</LayoutGrid>

<!-- {#if showdetail==true}
        <div class = "videodetail-container">
            <Videodetail videoInfo ={videodetail} on:closeEvent={e => showdetail=!e.detail}/> 
        </div>    
    {/if} -->
<!-- </div> -->

<style>
    .shadow-backdround {
        position: absolute;
        top: 4px;
        right: 4px;
        border-radius: 50%;
        width: 50px;
        height: 50px;
        background-color: rgb(0, 0, 0);
        z-index: 2;
        opacity: 0.7;
        display: flex;
    }
</style>
