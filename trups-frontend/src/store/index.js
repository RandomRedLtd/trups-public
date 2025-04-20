import {createStore} from "vuex";
import apiClient from "@/util/apiClient.js";

export default createStore({
    state() {
        return {
            apiKey: null,
            bridgeOnline: false,
            user: null,
            modelTypeString: {
                "LINEAR_REGRESSION": "Linear regression",
                "LOGISTIC_REGRESSION": "Logistic regression",
                "DECISION_TREE_CLASSIFIER": "Decision tree classifier",
                "DECISION_TREE_REGRESSOR": "Decision tree regressor"
            },
            statusString: {
                "IN_QUEUE": "Queued for training",
                "TRAINING_IN_PROGRESS": "Training in progress",
                "TRAINING_SUCCESS": "Training completed",
                "TRAINING_FAILURE": "Training unsuccessful"
            }
        }
    },
    mutations: {
        SET_API_KEY(state, payload) {
            state.apiKey = payload.apiKey;
        },
        SET_BRIDGE_ONLINE(state, payload) {
            state.bridgeOnline = payload.bridgeOnline;
        },
        SET_USER(state, payload) {
            state.user = payload.user;
        }
    },
    actions: {
        fetchApiKey({ commit, state }) {
            return apiClient.fetchApiKey().then(res => commit("SET_API_KEY", { apiKey: res }));
        },
        fetchSelf({ commit, state }) {
            return apiClient.fetchSelf(state.apiKey).then(res => commit("SET_USER", { user: res }));
        },
        cleanBridge({ commit, state }) {
            return apiClient.clean().then(() => commit("SET_API_KEY", { apiKey: null }));
        },
        setApiKey({ commit, state }, payload) {
            return apiClient.postApiKey(payload.apiKey).then(() => commit("SET_API_KEY", payload));
        },
        bridgeHealthCheck({ commit, state }) {
            return apiClient.bridgeHealthCheck()
                .then(() => commit("SET_BRIDGE_ONLINE", { bridgeOnline: true }))
                .catch(() => commit("SET_BRIDGE_ONLINE", { bridgeOnline: false }));
        },
        fetchAllModels({ state }, payload) {
            return apiClient.fetchAllModels(state.apiKey, payload.page);
        },
        fetchOneModel({ state }, payload) {
            return apiClient.fetchOneModel(state.apiKey, payload.id);
        },
        createModel({ state }, payload) {
            for (let i = 0; i < payload.model.inputFields.length; i++) {
                if (payload.model.inputFields[i].type === "ENUMERATION") {
                    payload.model.inputFields[i].enumeration = payload.model.inputFields[i].enumeration.split(",");
                }
            }

            return apiClient.createModel(state.apiKey, payload.model, payload.data);
        },
        checkInferenceAllowed({ state }, payload) {
            return apiClient.checkInferenceAllowed(state.apiKey, payload.modelId);
        },
        fetchAllModelAccess({ state }, payload) {
            return apiClient.fetchAllModelAccess(state.apiKey, payload.modelId);
        },
        updateModel({ state }, payload) {
            return apiClient.updateModel(state.apiKey, payload.modelId, payload.model);
        },
        deleteModel({ state }, payload) {
            return apiClient.deleteModel(state.apiKey, payload.modelId);
        },
        runInference({ state }, payload) {
            return apiClient.inference(state.apiKey, payload.modelId, payload.inference);
        },
        updateModelAccess({ state }, payload) {
            return apiClient.updateModelAccess(state.apiKey, payload.modelId, payload.userId, payload.mlModelAccess);
        }
    }
})
