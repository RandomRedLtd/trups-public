<template>
    <v-container style="height: 100%; background-color: #fcfcfc; border-radius: 8px">
        <v-row no-gutters justify="center" style="width: 100%">
            <v-col style="max-width: 800px" align-self="center">
                <h3>User info</h3>
                <p>User ID: {{ user.id }}</p>
                <p>Username: {{ user.username }}</p>
                <br>
                <h3>TruPS data reset</h3>
                <p>If you're experiencing any issues you may try cleaning your TruPS directory.</p>
                <p>This will delete your local TruPS directory which contains your API key, private keys, and model data.</p>
                <p>Note that if you have your API key all model data is recoverable, but private keys are not.</p>
                <br>
                <v-btn block color="red" size="x-large" @click="dialog = true">Clean</v-btn>
            </v-col>
        </v-row>
        <v-dialog v-model="dialog" width="auto">
            <v-card
                max-width="600"
                text="This will delete all your local files: API key, private keys, and model data, are you sure you want to do this?">
                <template v-slot:actions>
                    <v-btn size="x-large" @click="dialog = false">Cancel</v-btn>
                    <v-btn size="x-large" color="red" @click="clean" prepend-icon="mdi-alert-outline">Clean</v-btn>
                </template>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
import {mapActions, mapState} from "vuex";

export default {
    data() {
        return {
            dialog: false
        }
    },
    computed: {
      ...mapState(["user"])
    },
    methods: {
        ...mapActions(["cleanBridge"]),
        clean: function() {
            this.dialog = false;
            this.$router.push("/");
            this.cleanBridge();
        }
    }

}
</script>
