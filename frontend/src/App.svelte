<script lang="ts">
	import { onMount } from "svelte";
	import Videopreview from "./pages/Videopreview.svelte";
	import Videospool from "./pages/Videospool.svelte";
	import { infosByTag } from "./js/fetch";
	import Avatarlist from "./pages/Avatarlist.svelte";
	import Tagsnavteil from "./pages/Tagsnavteil.svelte";
	import json from "./pages/text.json";
	import { mdiDownload } from "@mdi/js";
	import { mdiMenu } from '@mdi/js';

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

	let data = json;
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
		<Tagsnavteil on:tagEvent={(e) => (data = e.detail)} />

		<!-- <List>
		  <Item
			href="javascript:void(0)"
			on:click={() => setActive('Gray Kittens')}
			activated={active === 'Gray Kittens'}
		  >
			<Text>Gray Kittens</Text>
		  </Item>
		  <Item
			href="javascript:void(0)"
			on:click={() => setActive('A Space Rocket')}
			activated={active === 'A Space Rocket'}
		  >
			<Text>A Space Rocket</Text>
		  </Item>
		  <Item
			href="javascript:void(0)"
			on:click={() => setActive('100 Pounds of Gravel')}
			activated={active === '100 Pounds of Gravel'}
		  >
			<Text>100 Pounds of Gravel</Text>
		  </Item>
		  <Item
			href="javascript:void(0)"
			on:click={() => setActive('All of the Shrimp')}
			activated={active === 'All of the Shrimp'}
		  >
			<Text>All of the Shrimp</Text>
		  </Item>
		  <Item
			href="javascript:void(0)"
			on:click={() => setActive('A Planet with a Mall')}
			activated={active === 'A Planet with a Mall'}
		  >
			<Text>A Planet with a Mall</Text>
		  </Item>
		</List> -->
	  </Content>
	</Drawer>
   
	<AppContent class="app-content">
	  <main class="main-content">
		<IconButton class="material-icons" on:click={() => (open = !open)} ripple={false}>
			<Icon tag="svg" viewBox="0 0 24 24">
				<path fill="currentColor" d={mdiMenu} />
			</Icon>
		</IconButton>
		<br />
		<Videospool pool={data} />
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
		min-height: 98% ;
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
