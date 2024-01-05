<script lang="ts">
	import { onMount } from "svelte";
	import Videopreview from "./pages/Videopreview.svelte";
	import Videospool from "./pages/Videospool.svelte";
	import Avatarlist from "./pages/Avatarlist.svelte";
	import Tagsnavteil from "./pages/Tagsnavteil.svelte";
	import { mdiDownload } from "@mdi/js";
	import { mdiMenu } from "@mdi/js";

	import IconButton, { Icon } from "@smui/icon-button";

	import Drawer, {
		AppContent,
		Content,
		Header,
		Title,
		Subtitle,
	} from "@smui/drawer";
	import Button, { Label } from "@smui/button";
	import List, { Item, Text } from "@smui/list";

	let open = false;
	let active = "Gray Kittens";

	function setActive(value: string) {
		active = value;
	}

	let data;

	//tag request
	import { infosByTag } from "./js/fetch.js";
	import { createEventDispatcher } from "svelte";
	let dispatch = createEventDispatcher();
	let selected_tag = "Test";
	let videos_by_tag_params = {
		cache_refresh_threshold: 1,
		offset: 1,
		limit: 20,
		allowed_media_sources: ["Local", "Youtube"],
		allowed_tagging_methods: ["YoutubeChannel"],
	};

	// $:console.log(selected_tag); 

	// (e) => {console.log("out");
	// 	infosByTag(selected_tag, params, (res) => {
	// 		dispatch("tagEvent", res);
	// 	});
	// 	data = e.datail;
	// };
	$:infosByTag(selected_tag, videos_by_tag_params, (res) => {
			data = res;
		});
</script>

<svelte:head>
	<link rel="stylesheet" href="./css/bare.css" />
	<link
		rel="stylesheet"
		href="./css/smui.css"
		media="(prefers-color-scheme: light)"
	/>
	<link
		rel="stylesheet"
		href="./css/smui-dark.css"
		media="screen and (prefers-color-scheme: dark)"
	/>
</svelte:head>

<div class="drawer-container">
	<Drawer variant="dismissible" bind:open>
		<Header>
			<Title>Media Subscrbiber</Title>
			<Subtitle>It's the best drawer.</Subtitle>
		</Header>
		<Content>
			<Tagsnavteil
				on:tagEvent={(e) => {
					selected_tag = e.detail;
				}}
			/>
		</Content>
	</Drawer>

	<AppContent class="app-content">
		<main class="main-content">
			<IconButton
				class="material-icons"
				on:click={() => (open = !open)}
				ripple={false}
			>
				<Icon tag="svg" viewBox="0 0 24 24">
					<path fill="currentColor" d={mdiMenu} />
				</Icon>
			</IconButton>
			<br />
			<Videospool />
		</main>
	</AppContent>
</div>

<!-- <div class="left-side-container">
	<div class="header-container"><h1>Media-subscrbiber</h1></div>
	<Tagsnavteil on:tagEvent={(e) => (data = e.detail)} />
</div>
<div class="right-side-container">
	<Avatarlist {data} />
	<Videospool pool={data} />
</div> -->

<!-- <div>
	<div style="display: flex; align-items: center;">
		<IconButton on:click={() => clicked++}>
			<Icon tag="svg" viewBox="0 0 24 24">
				<path fill="currentColor" d={mdiFormatColorFill} />
			</Icon>
		</IconButton>
	</div>
	<div style="display: flex; align-items: center;">
		<IconButton on:click={() => clicked++} disabled>
			<Icon tag="svg" viewBox="0 0 24 24">
				<path fill="currentColor" d={mdiWrench} />
			</Icon>
		</IconButton>&nbsp;(disabled)
	</div>
	<div style="display: flex; align-items: center;">
		<IconButton on:click={() => clicked++} ripple={false}>
			<Icon tag="svg" viewBox="0 0 24 24">
				<path fill="currentColor" d={mdiCurrencyUsd} />
			</Icon>
		</IconButton>&nbsp;(no ripple)
	</div>

	<pre class="status">Clicked: {clicked}</pre>
</div> -->

<!-- <Button color="light" on:click={e => {infosByTag("test", res => data =res)}}> 
	<Icon name="tags" size="2x"></Icon>
</Button> -->
<!-- <Avatarlist data ={data}/> -->

<style>
	.left-side-container {
		position: absolute;
		/* z-index:-1;  may used for background setting*/
		display: flex;
		flex-direction: column;
		height: 100%;
		width: 250px;
		border-right-style: solid;
		border-right-width: 6px;
		border-image: linear-gradient(to right, #b9b9b9, #ffffff) 1 100%;
	}
	.header-container {
		margin: 5px;
	}
	.right-side-container {
		position: absolute;
		top: 0;
		left: 253px;
		right: 0;
		bottom: 0;
		display: flex;
		flex-direction: column;
	}

	/* These classes are only needed because the
    drawer is in a container on the page. */
	.drawer-container {
		position: absolute;
		display: flex;
		min-height: 98%;
		width: 99%;
		border: 1px solid
			var(--mdc-theme-text-hint-on-background, rgba(0, 0, 0, 0.1));
		overflow: hidden;
		z-index: 0;
	}

	* :global(.app-content) {
		flex: auto;
		overflow: auto;
		position: relative;
		flex-grow: 1;
	}

	.main-content {
		overflow: auto;
		padding: 16px;
		height: 100%;
		box-sizing: border-box;
	}
</style>
