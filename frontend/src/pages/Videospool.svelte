<script lang="ts">
    import Videopreview from "./Videopreview.svelte";
    import Videodiaglog from "./Videodiaglog.svelte";
    //import { MasonryInfiniteGrid } from "@egjs/svelte-infinitegrid";

    let items = [];
    let videodetail;
    let pool;

    import LayoutGrid, { Cell } from "@smui/layout-grid";

    // let length = pool[0].videos_info.length
    // let publish =pool[0].videos_info.published
    // let title = pool[0].videos_info.title
    // let img = pool[0].videos_info.thumbnails[0].url
    // $:onMount(e =>console.log("pool:" + pool));

    let open = false;
    let selected = "Nothing yet";
    let active = "Information";

    import { videos_in_pool } from "../js/stores.js";
    import { infosByTag } from "../js/fetch";
    videos_in_pool.subscribe((value) => {
        pool = value;
    });
    //infinit grid
</script>

<Videodiaglog {videodetail} bind:open />
<LayoutGrid>
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
