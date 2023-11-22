<style scoped>

.addmargin {
    margin-top: 30px;
    margin-bottom: 30px;
}

.btn-success {
    background-color: green;
    color: white;
}

.btn-danger {
    background-color: red;
    color: white;
}

.table-striped tbody tr:nth-child(odd) {
    background-color: #f8f9fa; /* Set to your preferred grey shade */
}

.table-striped tbody tr:nth-child(even) {
    background-color: white;
}

.table td, .table th {
    border: 1px solid #dee2e6;
}

.spacing {
    margin-bottom: 15px;
}

.custom-input {
    margin-bottom: 10px;
}

</style>

<template>

<div class="home">
    <div class="col-md-6 mx-auto spacing">
        <p>This Page Displays the list of Applicants</p>

        <div class="row custom-input">
            <div class="col-md-6">
                <input type="text" class="form-control" v-model="newApplicantName" placeholder="New Applicant Name">
            </div>
            <div class="col-md-6">
                <button class="btn btn-primary" @click="addApplicant">Add Applicant</button>
            </div>
        </div>

        <div class="row custom-input">
            <div class="col-md-6">
                <input type="text" class="form-control" v-model="nameFilterValue" placeholder="Name Filter" @input="filterApplicants">
            </div>
            <div class="col-md-6">
                <div class="form-check">
                    <input type="checkbox" class="form-check-input" v-model="hideDeclined" @change="filterApplicants" id="hideDeclinedCheckbox">
                    <label class="form-check-label" for="hideDeclinedCheckbox">Hide Declined</label>
                </div>
            </div>
        </div>
        <table class="table table-striped spacing">
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="(applicant, index) in applicants" :key="applicant._id" :class="{ 'bg-white': index % 2 === 0, 'bg-lightgrey': index % 2 !== 0 }">
                    <td>{{applicant.name}}</td>
                    <td>{{applicant.status}}</td>
                    <td>
                        <template v-if="applicant.status === 'OPEN'">
                            <a class="btn btn-success" v-on:click="updateApplicantStatus(applicant._id, 'ACCEPTED')" role="button">
                                <span style="color:white">Annehmen</span>
                            </a>
                            <span> | </span>
                            <a class="btn btn-danger" v-on:click="updateApplicantStatus(applicant._id, 'DECLINED')" role="button">
                                <span style="color:white">Ablehnen</span>
                            </a>
                        </template>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

</template>

<script>

import axios from 'axios';

const APPLICANT_API_BASE_URL = 'http://localhost:8102';

export default {
    name: 'applicants-view',
    data() {
        return {
            applicants: [],
            newApplicantName: '',
            nameFilterValue: '',
            hideDeclined: false,
        };
    },
    mounted() {
        this.filterApplicants();
    },
    methods: {
        updateApplicantStatus(applicantId, status) {
            const url = `${APPLICANT_API_BASE_URL}/applicants?id=${applicantId}`;

            axios.patch(url, `"${status}"`, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                const updatedApplicant = response.data;
                const index = this.applicants.findIndex(applicant => applicant._id === applicantId);
                if (index !== -1) {
                  this.applicants.splice(index, 1, { ...updatedApplicant });
                }
            })
            .catch(error => {
                console.error(error);
            });
        },
        addApplicant() {
            const data = {
                name: this.newApplicantName,
            };

            axios.post(`${APPLICANT_API_BASE_URL}/applicants`, data, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                }
            })
            .then(() => {
                this.filterApplicants();
                this.newApplicantName = '';
            })
            .catch(error => {
                console.error(error);
            });
        },

        filterApplicants() {
            const params = {};

            if (this.nameFilterValue) {
                params.nameFilterValue = this.nameFilterValue;
            }
            params.hideDeclined = this.hideDeclined;

            axios({
                method: "GET",
                url: `${APPLICANT_API_BASE_URL}/applicants`,
                params: params,
            }).then(response => {
                this.applicants = response.data;
            }).catch(error => {
                console.error(error);
            });
        }
    }
}

</script>
