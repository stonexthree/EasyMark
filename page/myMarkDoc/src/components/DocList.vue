<template>
  <div class="list-area" :style="{width:widthPercent+'%' }">
    <div class="list-area-mask" v-show="loading">
      <n-icon class="loading-icon" size="32" color="rgba(200,200,200,1)">
        <Loading3QuartersOutlined/>
      </n-icon>
    </div>
    <n-list :show-divider=false hoverable clickable class="list">
      <n-list-item class="list-row" v-for="doc in docListComputed" @click="onRowClick(doc.docId)">
        <template #suffix>
          <p>{{ doc.updateTimestamp }}</p>
        </template>
        <div>
          <n-ellipsis style="width: calc(70% - 5px);padding-right: 5px">
            {{ doc.docName }}
          </n-ellipsis>
          <n-ellipsis style="width: 30%">
            {{ doc.docAuthor }}
          </n-ellipsis>
        </div>
      </n-list-item>
    </n-list>
  </div>
</template>

<script lang="ts">
import {DocInfo} from "../model/models";

export default {
  name: "DocList",
}
</script>

<script setup lang="ts">
import {NList, NListItem, NConfigProvider, darkTheme, NEllipsis, NIcon} from 'naive-ui';
import {Loading3QuartersOutlined} from '@vicons/antd'
import {onMounted, ref, computed, Ref} from 'vue'
import {DocApi} from '../api-define'
import axios from 'axios';
import {loginStatus} from '../globalStatus'
import {DocInfo} from '../model/models'
import {useRouter} from 'vue-router'

const props = defineProps({
  docs: Array,
  loading: Boolean,
  actionEnabled: Boolean,
  widthPercent: Number,
  showEditIcon: Boolean,
  resultEditable: Boolean
})

const docDemoList: DocInfo[] = DocInfo.mockArray(10);

const allDoc: Ref<DocInfo[]> = ref([]);
const docListComputed = computed(() => {
  if (props.docs !== undefined) {
    return props.docs;
  }
  return allDoc.value;
})


const router = useRouter();

function onRowClick(docId: string): void {
  //const docId = (event.target as Element).getAttribute('doc-id');
  //const element:Element = (event.target as Element);
  //console.log(element);
  if (props.resultEditable) {
    router.push('/edit-doc/' + docId);
    return;
  }
  router.push('/doc/' + docId);
}

function loadList(){
  axios(DocApi.getAllDoc()).then(
      (response) => {
        if (response.data.code === '00000') {
          for (const i in response.data.data) {
            const doc = response.data.data[i];
            allDoc.value.push(new DocInfo(doc.docId, doc.docName, doc.authorNickname, doc.updateTimestamp));
          }
          console.log(allDoc.value);
        }
        if (response.data.code === 'A0200') {
          //loginStatus.loginFailed();
        }
      }
  ).catch()
}

onMounted(
    () => {
      if (props.docs !== undefined) {
        return;
      }
      loadList();
      loginStatus.registerAction(loadList)
    }
)

</script>

<style scoped>
.list-area {
  position: absolute;
  top: 20px;
  width: 100%;
  min-width: 120px;
  background-color: #2e3440;
  height: calc(100% - 30px);
  overflow: scroll;
  left: 50%;
  transform: translateX(-50%);
}

.list-area::-webkit-scrollbar {
  display: none;
}

.list-area-mask {
  position: absolute;
  width: 100%;
  height: 100%;
}

.loading-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: rotation;
  animation-iteration-count: infinite;
  animation-duration: 2s;
}

@keyframes rotation {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>