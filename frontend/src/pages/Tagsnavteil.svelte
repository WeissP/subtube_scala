<script>
    //import Fab, { Label, Icon } from '@smui/fab';
    import { infosByTag } from "../js/fetch.js";
    import { createEventDispatcher } from "svelte";
    import Button, { Label } from "@smui/button";
    import Chip, { Set, LeadingIcon, TrailingIcon, Text } from "@smui/chips";

    let dispatch = createEventDispatcher();
    let tags = ["Test", "Sport", "Music", "News", "Moive"];
    let selected_tag = "Test";
    let videos_by_tag_params = {
		cache_refresh_threshold: 1,
		offset: 1,
		limit: 20,
		allowed_media_sources: ["Local", "Youtube"],
		allowed_tagging_methods: ["YoutubeChannel"],
	};

    //default parameters
    // let params = {
    //     cache_refresh_threshold: 1,
    //     offset: 1,
    //     limit: 20,
    //     allowed_media_sources: ["Local", "Youtube"],
    //     allowed_tagging_methods: ["YoutubeChannel"],
    // };

    $:infosByTag(selected_tag, videos_by_tag_params, (res) => {
			dispatch("videosEvent", res);;
		});
</script>

<div class="searchbar-container color-button">
    <input type="text" placeholder="Search.." name="search" />
</div>

<!-- (e) => {
    infosByTag("test", (res) => {
        dispatch("tagEvent", res);
    });
} -->

<!-- (e) => {
    infosByTag(chip, params, (res) => {
        dispatch("tagEvent", res);
    });
} -->
<div class="tags-container">
    <div class="tag-containe">
        <Set chips={tags} let:chip choice bind:selected={selected_tag}>
            <Chip
                {chip}
                shouldFocusPrimaryActionOnClick={false}
                on:click={() =>{dispatch("tagEvent", chip)}}
            >
                <Text>{chip}</Text>
            </Chip>
        </Set>
        <pre class="status">Selected: {selected_tag}</pre>
        <!-- <Button on:click={() => (selected = tags[1])}>
            <Label>{tags[1]}</Label>
          </Button>
          <Button on:click={() => (selected = tags[2])}>
            <Label>{tags[2]}</Label>
          </Button>
          <Button on:click={() => (selected = tags[3])}>
            <Label>{tags[3]}</Label>
          </Button>
          <Button on:click={() => (selected = tags[4])}>
            <Label>{tags[4]}</Label>
          </Button> 
         <Button 
            on:click={(e) => {
                infosByTag("test", (res) => {
                    dispatch("tagEvent", res);
                });
            }}
            variant="outlined"
            color = "primary"
            ><Label>Test</Label></Button
        > -->

        <!-- <Button variant="outlined"><Label>Sport</Label></Button>
        <Button variant="outlined"><Label>Music</Label></Button>
        <Button variant="outlined"><Label>News</Label></Button>
        <Button variant="outlined"><Label>Moive</Label></Button> -->
    </div>
</div>

<style>
    .color-button {
        color: black;
    }
    .searchbar-container {
        margin: 10px;
        height: 30px;
    }
    .tags-container {
        margin: 5px;
        display: flex;
    }
    .tag-container {
        margin: 3px;
        min-width: 200px;
        overflow: hidden;
    }
</style>
