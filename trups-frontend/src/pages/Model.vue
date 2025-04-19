<template>
    <v-container style="height: 100%; background-color: #fcfcfc; border-radius: 8px">
        <v-row no-gutters justify="center" style="width: 100%">
            <v-col style="max-width: 800px" align-self="center">
                <h3>Model ID: {{ model.id }}</h3>
                <h3>Model name: {{ model.name }}</h3>
                <h3>Description: {{ model.description }}</h3>
                <h3>Model type: {{ modelTypeString[model.type] }}</h3>
                <h3>Status: {{ statusString[model.status] }}</h3>
                <h3>Score: {{ model.score ? model.score : 'N/A' }}</h3>
                <h3>Public access: <v-icon v-if="model.publicAccess" color="green" icon="mdi-check"></v-icon> <v-icon v-else icon="mdi-close" color="red"></v-icon></h3>
                <h3>Enabled: <v-icon v-if="model.enabled" color="green" icon="mdi-check"></v-icon> <v-icon v-else icon="mdi-close" color="red"></v-icon></h3>
                <template v-if="model.status === 'TRAINING_FAILURE'">
                    <br>
                    <h2 style="color: red">Training for this model was not successful, no actions are allowed on this model</h2>
                    <br>
                </template>
                <template v-if="(user.id === model.owner || user.role === 'ROLE_ADMIN') && model.status === 'TRAINING_SUCCESS'">
                    <br>
                    <hr>
                    <br>
                    <h3>Actions</h3>
                    <br>
                    <v-btn @click="enableDisableModel">
                        <span v-if="model.enabled">Disable</span>
                        <span v-else>Enable</span> model
                    </v-btn>
                    <br>
                    <br>
                    <template v-if="!model.publicAccess">
                        <p><strong>Users with access to this model:</strong></p>
                        <p>{{ userAccess.length > 0 ? userAccess : "There are no user with access to this model" }}</p>
                        <br><br>
                        <p><strong>Edit access</strong></p>
                        <v-number-input
                            variant="outlined"
                            control-variant="hidden"
                            :label="'User ID (number)'"
                            :precision="0"
                            v-model="granteeId"
                            style="max-width: 200px;"></v-number-input>
                        <v-btn @click="handleAccessChange(true)" :disabled="!granteeId">Give access</v-btn>
                        <v-btn @click="handleAccessChange(false)" :disabled="!granteeId">Revoke access</v-btn>
                        <br>
                        <br>
                        <br>
                    </template>
                    <v-btn v-if="user.role === 'ROLE_ADMIN'" color="red" @click="handleDelete">Delete model</v-btn>
                    <br>
                </template>
                <br>
                <hr>
                <br>
                <template v-if="inferenceAllowed && model.status === 'TRAINING_SUCCESS' && model.enabled">
                    <h3>Inference</h3>
                    <v-form fast-fail @submit.prevent v-model="valid">
                        <template v-for="(input, index) in model.inputFields">
                            <v-number-input
                                v-if="input.type === 'INTEGER' || input.type === 'FLOAT'"
                                :precision="input.type === 'INTEGER' ? 0 : 4"
                                v-model="inference[input.name]"
                                required
                                variant="outlined"
                                :rules="[v => !!v || `${input.name} is required`]"
                                :label="input.name"
                                :hint="`${input.type} number`"
                                persistent-hint
                                control-variant="hidden">
                            </v-number-input>
                            <v-select
                                v-else
                                persistent-hint
                                :items="input.enumeration"
                                :label="input.name"
                                :rules="[v => (!!v || `${input.name} is required`)]"
                                variant="outlined"
                                v-model="inference[input.name]"
                                required>
                            </v-select>
                        </template>
                    </v-form>
                    <br>
                    <v-btn size="x-large" :disabled="!valid || inferenceRunning" @click="handleInference">Run inference</v-btn>
                    <br><br>
                    <template v-if="inferenceRunning">
                        <h3>Inference running... do not navigate away from this page</h3>
                        <v-progress-circular
                            :size="70"
                            :width="7"
                            color="purple"
                            indeterminate
                        ></v-progress-circular>
                    </template>
                    <h3 v-if="inferenceOutput">Inference output: {{ inferenceOutput }}</h3>
                </template>
                <template v-else>
                    <h3>You are not allowed inference on this model</h3>
                </template>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import {mapActions, mapState} from "vuex";

export default {
    data() {
        return {
            showAccess: false,
            userAccess: [],
            model: {},
            inference: {},
            inferenceOutput: null,
            inferenceRunning: false,
            valid: false,
            granteeId: null,
            inferenceAllowed: false
        }
    },
    computed: {
        ...mapState(["user", "statusString", "modelTypeString"]),
    },
    methods: {
        ...mapActions(["fetchOneModel", "checkInferenceAllowed", "fetchAllModelAccess", "updateModel", "deleteModel", "runInference", "updateModelAccess"]),
        enableDisableModel: function() {
            this.inferenceOutput = null;
            this.model.enabled = !this.model.enabled;
            this.updateModel({ modelId: this.model.id, model: { enabled: this.model.enabled }});
        },
        handleDelete: function() {
            this.deleteModel({ modelId: this.model.id }).then(res => this.$router.push("/"));
        },
        handleInference: function() {
            this.inferenceOutput = null;
            this.inferenceRunning = true;
            this.runInference({ modelId: this.model.id, inference: this.inference }).then(res => res.json()).then(res => { this.inferenceOutput = res.output; this.inferenceRunning = false });
        },
        handleAccessChange: function(enable) {
            this.updateModelAccess({ modelId: this.model.id, userId: this.granteeId, mlModelAccess: { enabled: enable } })
                .then(res => this.fetchAllModelAccess({ modelId: this.model.id }).then(res => res.json()).then(res => this.userAccess = res.filter(o => o.enabled).map(o => o.id?.appUserId)));
            this.granteeId = null;
        }
    },
    created() {
        this.fetchOneModel({ id: this.$route.params.id })
            .then(res => res.json())
            .then(res => {
                this.model = res

                if ((this.user.id === this.model.owner) || this.model.publicAccess || (this.user.role === "ROLE_ADMIN")) {
                    this.inferenceAllowed = true;
                } else {
                    this.checkInferenceAllowed({ modelId: this.$route.params.id }).then(res => res.json()).then(res => this.inferenceAllowed = res.enabled);
                }

                if (this.model.owner === this.user.id || this.user.role === "ROLE_ADMIN") {
                    this.fetchAllModelAccess({ modelId: this.model.id }).then(res => res.json()).then(res => this.userAccess = res.filter(o => o.enabled).map(o => o.id?.appUserId));
                }
            })
            .catch(e => e)
    }

}
</script>
