<script lang="ts">
    export let open, videodetail;
    import Videodetail from "./Videodetail.svelte";

    import Dialog, { Title, Content, Actions } from "@smui/dialog";

    import Button, { Label } from "@smui/button";
    import Paper from "@smui/paper";

    let openNoPadding = false;

    import SegmentedButton, { Segment } from "@smui/segmented-button";

    let choices = ["Audio", "Video"];
    //buttom list
    import Menu from "@smui/menu";

    import Card, {
        PrimaryAction,
        ActionButtons,
        ActionIcons,
    } from "@smui/card";

    import Tab from "@smui/tab";
    import TabBar from "@smui/tab-bar";
    let active = "Information";

    $: if (open == false) {
        active = "Information";
    }

    import Checkbox from "@smui/checkbox";
    import FormField from "@smui/form-field";


    let video_options = [
        {
            name: "360p",
            disabled: false,
        },
        {
            name: "720p",
            disabled: true,
        },
        {
            name: "1080p",
            disabled: false,
        },
        {
            name: "2k",
            disabled: false,
        },
        {
            name: "4k",
            disabled: false,
        },
    ];
    let selected = ["1080p"];
    
</script>

<Dialog
    bind:open
    noContentPadding
    sheet
    aria-describedby="sheet-no-padding-content"
    surface$style="width: 640px; max-width: calc(100vw - 32px);"
>
    <Content id="sheet-content">
        <TabBar tabs={["Information", "Downlaod"]} let:tab bind:active>
            <Tab {tab}>
                <Label>{tab}</Label>
            </Tab>
        </TabBar>
        {#if active === "Information"}
            <Paper variant="unelevated">
                <Content
                    >{#if videodetail}
                        <Videodetail videoInfo={videodetail} />
                    {/if}</Content
                >
            </Paper>
        {:else if active === "Downlaod"}
            <Paper variant="unelevated">
                <Content>
                    <p>Video download</p>
                    {#each video_options as option}
                        <FormField>
                            <Checkbox
                                bind:group={selected}
                                value={option.name}
                                disabled={option.disabled}
                            />
                            <span slot="label" style="color:black"
                                >{option.name}{option.disabled
                                    ? " (disabled)"
                                    : ""}</span
                            >
                        </FormField>
                    {/each}
                    <p>Audio download</p>
                    {#each video_options as option}
                        <FormField>
                            <Checkbox
                                bind:group={selected}
                                value={option.name}
                                disabled={option.disabled}
                            />
                            <span slot="label" style="color:black"
                                >{option.name}{option.disabled
                                    ? " (disabled)"
                                    : ""}</span
                            >
                        </FormField>
                    {/each}
                </Content> 
            </Paper>
            <Actions>
                <Button><p>Download</p></Button>
            </Actions>
        {/if}
    </Content>
    
</Dialog>

<style>
</style>
