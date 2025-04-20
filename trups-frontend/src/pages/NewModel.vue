<template>
    <v-container style="height: 100%; background-color: #fcfcfc; border-radius: 8px">
        <v-row no-gutters justify="center" style="width: 100%">
            <v-col style="max-width: 800px" align-self="center">
                <v-form fast-fail @submit.prevent v-model="valid">
                    <v-text-field
                        variant="outlined"
                        v-model="newModel.name"
                        required
                        :rules="[v => !!v || 'Model name is required']"
                        label="Model name"
                    ></v-text-field>
                    <v-text-field
                        variant="outlined"
                        v-model="newModel.description"
                        label="Model description"
                    ></v-text-field>
                    <v-select
                        variant="outlined"
                        item-title="name"
                        item-value="val"
                        v-model="newModel.type"
                        :items="modelTypes"
                        :rules="[v => !!v || 'Model type is required']"
                        label="Model type"
                        required
                    ></v-select>
                    <v-switch
                        v-model="newModel.publicAccess"
                        color="primary"
                        label="Public"
                        :inset="true"
                        hide-details
                    ></v-switch>
                    <br>
                    Y values column must be named 'Target', <strong>case sensitive</strong>
                    <v-file-input
                        variant="outlined"
                        accept="text/csv"
                        label="Model CSV data"
                        required
                        :rules="[v => !!v || 'Data file is required']"
                        v-model="data"
                    ></v-file-input>
                    <template v-for="(input, index) in newModel.inputFields">
                        <div style="border: 1px solid black; margin: 8px 0px; padding: 8px; border-radius: 8px">
                            <v-text-field
                                v-model="input.name"
                                required
                                variant="outlined"
                                :rules="[v => !!v || 'Name is required', v => newModel.inputFields.filter(o => o.name === v).length <= 1 || 'Input with that name already exists']"
                                label="Name"
                            ></v-text-field>
                            <v-select
                                variant="outlined"
                                item-title="name"
                                item-value="val"
                                v-model="input.type"
                                :items="inputTypes"
                                :rules="[v => (!!v || 'Input type is required')]"
                                label="Input type"
                                required
                            ></v-select>
                            <v-text-field
                                variant="outlined"
                                v-if="input.type === 'ENUMERATION'"
                                v-model="input.enumeration"
                                required
                                :rules="[v => !!v || 'Enumeration is required']"
                                label="Enumeration"
                                hint="Coffee,Tea,Water"
                                persistent-hint
                            ></v-text-field>
                        </div>
                    </template>
                </v-form>
                <v-row justify="space-between" style="padding: 32px;">
                    <v-btn @click="clear">Clear</v-btn>
                    <v-btn :disabled="!valid" @click="createNewModel">Create</v-btn>
                </v-row>
            </v-col>
        </v-row>
        <v-snackbar
            v-model="snackbar"
            :timeout="5000">
            <h2>Model successfully created, ID: {{ newModelId }}</h2>
            <template v-slot:actions>
                <v-btn
                    color="blue"
                    variant="text"
                    @click="$router.push(`/models/${newModelId}`)">

                    Go to model
                </v-btn>
            </template>
        </v-snackbar>
    </v-container>
</template>

<script>
import {mapActions} from "vuex";

export default {
    data() {
        return {
            valid: false,
            snackbar: false,
            newModelId: null,
            newModel: {
                name: null,
                description: null,
                type: null,
                publicAccess: true,
                inputFields: []
            },
            data: null,
            modelTypes: [
                { name: "Linear regression", val: "LINEAR_REGRESSION" },
                { name: "Logistic regression", val: "LOGISTIC_REGRESSION" },
                { name: "Decision tree classifier", val: "DECISION_TREE_CLASSIFIER" },
                { name: "Decision tree regressor", val: "DECISION_TREE_REGRESSOR" }
            ],
            inputTypes: [{ name: "Integer", val: "INTEGER" }, { name: "Float", val: "FLOAT" }, { name: "Enumeration", val: "ENUMERATION" }]
        }
    },
    methods: {
        ...mapActions(["createModel"]),
        createNewModel: function() {
            this.newModel.inputFields = this.newModel.inputFields.map(o => o.name === "Enumeration" ? o.split(",").map(o => o.trim()) : o);
            this.createModel({ model: this.newModel, data: this.data })
                .then(res => { this.snackbar = true; this.clear(); return res.json() })
                .then(res => this.newModelId = res.id)
                .catch(e => e);

        },
        clear: function() {
            this.newModel = {
                name: null,
                description: null,
                type: null,
                publicAccess: true,
                inputFields: []
            }

            this.data = null;
        }
    },
    watch: {
        data: function(newVal, oldVal) {
            if (!newVal)
                this.newModel.inputFields = [];

            if (newVal) {
                const reader = new FileReader();
                reader.readAsText(newVal);
                reader.onload = (e) => {
                    this.newModel.inputFields = reader.result.split('\n').shift().split(",").map(o => o.trim()).filter(o => o !== "Target").map(o => ({ name: o, type: null, enumeration: null }));
                }
            }
        }
    }
}
</script>
