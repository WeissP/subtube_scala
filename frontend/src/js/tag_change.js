import { createEventDispatcher } from "svelte";

let selected_tag = "Test";
let videos_by_tag_params = {
    cache_refresh_threshold: 1,
    offset: 1,
    limit: 20,
    allowed_media_sources: ["Local", "Youtube"],
    allowed_tagging_methods: ["YoutubeChannel"],
};


let nav_listner = (res) =>{selected_tag = res}