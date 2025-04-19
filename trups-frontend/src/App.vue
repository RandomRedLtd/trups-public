<template>
    <v-app style="background-color: #dcddd8; overflow: auto">
        <v-main>
            <v-container style="max-width: 1920px; min-width: 1920px; height: 100%">
                <v-row>
                    <v-col cols="2" style="height: 100%; font-size: 16px; font-weight: bold">
                        <v-icon :color="bridgeOnline ? '#3bc72c' : '#ff0000'" icon="mdi-circle" size="x-large"></v-icon>
                        Bridge status: {{ bridgeOnline ? "UP" : "DOWN" }}
                    </v-col>
                </v-row>
                <template v-if="loadingDone || $route.path === '/policy'">
                    <v-row style="height: calc(100% - 32px)">
                        <template v-if="$route.path === '/policy'">
                            <router-view />
                        </template>
                        <template v-else-if="!bridgeOnline || !apiKey">
                            <v-row align="center" justify="center" style="height: 100%">
                                <v-col cols="12" style="max-width: 700px;" class="d-flex flex-column justify-center align-center">
                                    <v-img style="margin-bottom: 32px" :width="256" cover src="@/assets/trups.png"></v-img>
                                    <template v-if="!bridgeOnline">
                                        <h1>TruPS bridge is unavailable, make sure it is running and that port 8282 is exposed when running a Docker container</h1>
                                    </template>
                                    <template v-else>
                                        <h1>To get started enter your API key</h1>
                                        <v-text-field v-model="apiKeyInput" style="width: 100%"></v-text-field>
                                        <v-btn @click.prevent="handleSetApiKey">Set API key</v-btn>
                                    </template>
                                </v-col>
                            </v-row>
                        </template>
                        <template v-else>
                            <v-col cols="2">
                                <Sidebar></Sidebar>
                            </v-col>
                            <v-col cols="10" style="padding: 8px">
                                <router-view />
                            </v-col>
                        </template>
                    </v-row>
                </template>
            </v-container>
        </v-main>
    </v-app>
</template>

<script>
import Sidebar from "@/components/Sidebar.vue";
import { mapActions, mapState } from "vuex";

export default {
    data() {
        return {
            apiKeyInput: null,
            loadingDone: false
        }
    },
    computed: {
        ...mapState(['apiKey', 'bridgeOnline'])
    },
    watch: {
        bridgeOnline: function(newVal, oldVal) {
            if (newVal)
                this.fetchApiKey().then(res => this.fetchSelf());
        }
    },
    methods: {
        ...mapActions(['fetchApiKey', 'setApiKey', 'bridgeHealthCheck', 'fetchSelf']),
        handleSetApiKey: function() {
            this.setApiKey({ apiKey: this.apiKeyInput }).then(res =>  { this.fetchSelf(); this.apiKeyInput = null });
        }
    },
    created() {
        Promise.all([this.bridgeHealthCheck(), this.fetchApiKey()])
            .then(res => this.fetchSelf())
            .finally(() => { this.loadingDone = true });
        setInterval(() => { this.bridgeHealthCheck() }, 2000);
    },
    components: {
        Sidebar
    },
}

</script>
