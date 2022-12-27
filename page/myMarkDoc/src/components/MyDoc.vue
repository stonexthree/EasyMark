<template>
  <DocList :result-editable="true" :docs-api-config="docApiConfig" :loading="loading" :width-percent="widthPercent" />
</template>

<script lang="ts">
import DocList from "./DocList.vue";
export default {
  name: "MyDoc",
  components: {DocList}
}
</script>
<script setup lang="ts" >
import {onMounted,Ref,ref} from 'vue';
import {DocInfo} from "../model/models";
import axios from "axios";
import {DocApi} from "../api-define";
import {loginStatus} from "../globalStatus";

defineProps({
  docs: Array,
  loading: Boolean,
  actionEnabled: Boolean,
  widthPercent: Number,
  showEditIcon: Boolean
})

const docApiConfig = ref<Object>(DocApi.getMyDocs());


</script>

<style scoped>
</style>