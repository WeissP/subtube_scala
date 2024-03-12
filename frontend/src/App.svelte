<script lang="ts">
	import { onMount } from "svelte";
	import Videopreview from "./pages/Videopreview.svelte";
	import Videospool from "./pages/Videospool.svelte";
	import Avatarlist from "./pages/Avatarlist.svelte";
	import Tagsnavteil from "./pages/Tagsnavteil.svelte";
	import { mdiDownload } from "@mdi/js";
	import { mdiMenu } from "@mdi/js";
	import IconButton, { Icon } from "@smui/icon-button";
	import { writable } from "svelte/store";
	import { videos_in_pool, testdata } from "./js/stores.js";
	import { DefaultApi } from "./genstub/api";
	import type { AxiosPromise, AxiosInstance, AxiosRequestConfig } from 'axios';


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

	// const config: AxiosRequestConfig ={
	// 	baseURL: "http://127.0.0.1:9090",
	// }
	let newapi = new DefaultApi();

	newapi.getV10YoutubeTagTagVideos("Test", undefined,0,2,["Youtube"],["YoutubeChannel","YoutubeVideo"]).then((res) => {
		console.log(res.data);
	}).catch((error)=>{console.log("catched error: " + error)});

	// let testdata_value;
	// let data_value;
	// testdata.subscribe((value) => {
	// 	testdata_value = value
	// });
	// data.subscribe((value) => {
	// 	data_value = value
	// });
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

<!-- <h1>The count is {testdata_value}</h1>
<h1>The value is {data_value}</h1> -->
<!-- <div class="drawer-container">
	<Drawer variant="dismissible" bind:open >
		<Header>
			<Title>Media Subscrbiber</Title>
			<Subtitle>It's the best drawer.</Subtitle>
		</Header>
		<Content>
			<Tagsnavteil on:videosEvent={(e) => {
				// pool = e.detail;
				console.log("get data:" + e.detail)
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
</div> -->

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
