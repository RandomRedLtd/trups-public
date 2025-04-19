const bridge = "http://localhost:8282"
const backend = "https://trups.app/api"

export default {
    postApiKey(_apiKey) {
        return fetch(`${bridge}/api-key`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ "apiKey": _apiKey }) });
    },
    fetchApiKey() {
        return fetch(`${bridge}/api-key`)
            .then(res => res.json())
            .then(res => res.apiKey);
    },
    fetchSelf(apiKey) {
        return fetch(`${backend}/users/self`, { headers: { "X-Api-Key": apiKey } }).then(res => res.json());
    },
    clean() {
        return fetch(`${bridge}/clean`);
    },
    bridgeHealthCheck() {
        return fetch(`${bridge}/health`);
    },
    fetchOneModel(apiKey, id) {
        return fetch(`${backend}/mlmodels/${id}/`, { headers: { "X-Api-Key": apiKey } });
    },
    fetchAllModels(apiKey, page) {
        return fetch(`${backend}/mlmodels/?page=${page}&sort=id,asc`, { headers: { "X-Api-Key": apiKey } });
    },
    createModel(apiKey, model, data) {
        const form = new FormData();
        form.append("data", new Blob([data], { type: "text/csv" }), "data.csv");
        form.append("model", new Blob([JSON.stringify(model)], { type: "application/json" }));

        return fetch(`${backend}/mlmodels/`, {
            method: "POST",
            headers: { "X-Api-Key": apiKey },
            body: form
        });
    },
    checkInferenceAllowed(apiKey, modelId) {
        return fetch(`${backend}/mlmodelaccess/${modelId}`, { headers: { "X-Api-Key": apiKey } });
    },
    fetchAllModelAccess(apiKey, modelId) {
        return fetch(`${backend}/mlmodelaccess/${modelId}/all`, { headers: { "X-Api-Key": apiKey } });
    },
    updateModel(apiKey, modelId, payload) {
        return fetch(`${backend}/mlmodels/${modelId}`, { method: "PATCH",  headers: { "X-Api-Key": apiKey, "Content-Type": "application/json" }, body: JSON.stringify(payload) });
    },
    deleteModel(apiKey, modelId) {
        return fetch(`${backend}/mlmodels/${modelId}`, { method: "DELETE",  headers: { "X-Api-Key": apiKey } });
    },
    inference(apiKey, modelId, payload) {
        return fetch(`${bridge}/inference/${modelId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });
    },
    updateModelAccess(apiKey, modelId, userId, payload) {
        return fetch(`${backend}/mlmodelaccess/${modelId}/${userId}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json", "X-Api-Key": apiKey },
            body: JSON.stringify(payload)
        })
    }

}
