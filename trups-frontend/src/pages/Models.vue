<template>
    <v-container style="height: 100%; background-color: #fcfcfc; border-radius: 8px">
        <v-row no-gutters>
            <v-col cols="12">
                <v-data-table-server
                    @click:row="handleClick"
                    :items-per-page="pageSize"
                    :page="page"
                    :headers="headers"
                    :items="models"
                    :items-length="totalCount"
                    style="background-color: #fcfcfc;"
                    @update:options="fetchModels">
                    <template #[`header.score`]="{ column }">
                        <v-tooltip text="Score value for models is calculated using default scoring method for that model type in scikit-learn">
                            <template #activator="{ props }">
                                <div class="v-data-table-header__content">
                                    <div class="v-data-table-header__content">
                                        <span>{{ column.title }}</span>&nbsp;&nbsp;
                                    </div>
                                    <v-icon v-bind="props" icon="mdi-information-outline" />
                                </div>
                            </template>
                        </v-tooltip>
                    </template>
                    <template v-slot:body="{ items }">
                        <tr v-for="item in models" :key="item.id" @click="handleClick(item.id)">
                            <td>
                                <v-tooltip v-if="this.user.id === item.owner" text="This is your model, if you would like to know more details about other models, please contact info@randomred.eu.">
                                    <template v-slot:activator="{ props }">
                                        <v-icon v-bind="props" icon="mdi-account"></v-icon>
                                    </template>
                                </v-tooltip>
                            </td>
                            <td>{{ item.id }}</td>
                            <td>{{ item.name }}</td>
                            <td>{{ modelTypeString[item.type] }}</td>
                            <td>{{ item.enabled ? "Yes" : "No" }}</td>
                            <td>{{ statusString[item.status] }}</td>
                            <td>{{ item.score ? item.score : "N/A" }}</td>
                            <td>{{ item.publicAccess ? "Yes" : "No" }}</td>
                        </tr>
                    </template>
                    <template v-slot:bottom>
                        <div class="text-center pt-2">
                            <v-pagination
                                v-model="page"
                                :length="totalPages"
                            ></v-pagination>
                        </div>
                    </template>
                </v-data-table-server>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import {mapActions, mapState} from "vuex";

export default {
    data() {
        return {
            headers: [
                { title: "", key: "owner", value: item => this.user === item.owner ? "mine" : "not mine" },
                { title: "Id", key: "id" },
                { title: "Name", key: "name" },
                { title: "Model type", key: "type", value: item => this.modelTypeString[item.type] },
                { title: "Available for inference", key: "enabled", value: item => item.enabled ? "Yes" : "No" },
                { title: "Status", key: "status", value: item =>  this.statusString[item.status]},
                { title: "Score", key: "score", value: item => item.score ? item.score : "N/A" },
                { title: "Public access", key: "publicAccess", value: item => item.publicAccess ? "Yes" : "No" }
            ],
            models: [],
            page: 1,
            pageSize: 20,
            totalPages: 0,
            totalCount: 0
        }
    },
    computed: {
        ...mapState(["modelTypeString", "statusString", "user"])
    },
    methods: {
        ...mapActions(['fetchAllModels']),
        fetchModels: function({ page, itemsPerPage, sortBy }) {
            this.fetchAllModels({ page: page - 1 })
                .then(res => {
                    this.totalCount = res.headers.get("X-Total-Count");
                    this.totalPages = Math.ceil(this.totalCount / this.pageSize);
                    return res.json();
                })
                .then(res => {
                    this.models = res;
                })
                .catch(e => e);
        },
        handleClick: function(id) {
            this.$router.push(`/models/${id}`);
        },
    }

}
</script>

<style>
.v-data-table__tbody > tr:hover {
    background-color: #444444;
    color: #fcfcfc;
    cursor: pointer;
}

</style>
