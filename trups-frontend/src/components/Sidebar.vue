<template>
        <v-row style="padding: 8px">
            <v-col xs="12" style="padding: 0px 0px; height: 100%; width: 100%; background-color: #fcfcfc; border-radius: 12px;">
                <v-row style="margin: 8px 0px">
                    <v-btn :ripple="false" :class="{ trupsactive: $route.fullPath === '/' }" style="width: 100%" prepend-icon="mdi-list-box-outline" variant="text" size="x-large" @click="models">
                        Models
                    </v-btn>
                </v-row>
                <v-row style="margin: 8px 0px">
                    <v-btn :ripple="false" :class="{ trupsactive: $route.fullPath === '/newmodel' }" style="width: 100%" prepend-icon="mdi-robot-outline" variant="text" size="x-large" @click="newModel">
                        New model
                    </v-btn>
                </v-row>
                <v-row style="margin: 8px 0px">
                    <v-btn :ripple="false" :class="{ trupsactive: $route.fullPath === '/settings' }" style="width: 100%" prepend-icon="mdi-cog" variant="text" size="x-large" @click="settings">
                        Settings
                    </v-btn>
                </v-row>
            </v-col>
        </v-row>
</template>

<script>
export default {
    data: function() {
        return {

        }
    },
    methods: {
        models: function() {
            this.$router.push("/")
        },
        settings: function() {
            this.$router.push("/settings")
        },
        newModel: function() {
            this.$router.push("/newmodel")
        }
    }
}

</script>

<style>
.trupsactive {
    background-color: #444444 !important;
    color: #fdfdfd !important;
}

</style>
