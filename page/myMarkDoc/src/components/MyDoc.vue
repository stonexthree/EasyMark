<template>
  <DocList :result-editable="true" :docs="myDocs" :loading="loading" :width-percent="widthPercent" />
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

const myDocs:Ref<DocInfo[]> = ref([])
function getMyDoc(){
  axios(DocApi.getMyDocs()).then(
      (response) => {
        if (response.data.code === '00000') {
          for (const i in response.data.data) {
            const doc = response.data.data[i];
            myDocs.value.push(new DocInfo(doc.docId, doc.docName, doc.authorNickname, doc.updateTimestamp));
          }
        }
        if (response.data.code === 'A0200') {
          //loginStatus.loginFailed();
        }
      }
  ).catch()
}
onMounted(
    ()=>{
      getMyDoc();
      loginStatus.registerAction(getMyDoc);
    }
)

</script>

<style scoped>
</style>