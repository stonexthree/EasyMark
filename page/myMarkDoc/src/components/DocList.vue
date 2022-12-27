<template>
  <div class="component-root" :style="rootStyle">
    <n-empty description="无数据" size="large" v-show="docArray.length === 0&&!loading" style="position: absolute;left: 50%;top: 50%;transform: translate(-50%,-50%)" />
    <div class="list-area-mask" v-show="loading">
      <n-icon class="loading-icon" size="32" :color=loadingColor>
        <Loading3QuartersOutlined/>
      </n-icon>
    </div>
    <n-list :show-divider=true hoverable clickable class="list">
      <n-list-item class="list-row" v-for="doc in docArray" @click="onRowClick(doc.docId)">
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
import {NList, NListItem, NConfigProvider, darkTheme, NEllipsis, NIcon,NEmpty} from 'naive-ui';
import {Loading3QuartersOutlined} from '@vicons/antd'
import {onMounted, ref, computed, Ref,watch} from 'vue'
import {DocApi} from '../api-define'
import axios from 'axios';
import {loginStatus} from '../globalStatus'
import {DocInfo} from '../model/models'
import {useRouter} from 'vue-router'
import {customComponentThemeProvider} from '../theme'
import {AxiosRequestConfig} from 'axios'

const props = defineProps({
  docsApiConfig: Object,
  //docs: Array,
  //loading: Boolean,
  actionEnabled: Boolean,
  widthPercent: Number,
  showEditIcon: Boolean,
  resultEditable: Boolean
})
const loading:Ref<boolean> = ref(false);
const docDemoList: DocInfo[] = DocInfo.mockArray(10);

const docArray: Ref<DocInfo[]> = ref([]);
/*
const docListComputed = computed(() => {
  /!*if (props.docs !== undefined) {
    return props.docs;
  }*!/
  return docArray.value;
})
*/
const a:string='a'

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
  loading.value=true;
  docArray.value=[];
  axios(<AxiosRequestConfig>props.docsApiConfig).then(
      (response) => {
        if (response.data.code === '00000') {
          for (const i in response.data.data) {
            const doc = response.data.data[i];
            docArray.value.push(new DocInfo(doc.docId, doc.docName, doc.authorNickname, doc.updateTimestamp));
          }
          //console.log(docArray.value);
        }
        /*if (response.data.code === 'A0200') {
          //loginStatus.loginFailed();
        }*/
        loading.value=false;
      }
  ).catch(()=>loading.value=false)
}

watch(props,async (newProps, oldProps) => {
  loadList();
})

onMounted(
    () => {
/*      if (props.docs !== undefined) {
        return;
      }*/
      loadList();
      loginStatus.registerAction(loadList)
    }
)

///////////////////////////
//动态样式控制
const rootStyle = computed(()=>{
  return{
  width:props.widthPercent + '%',
  backgroundColor: customComponentThemeProvider.value.colorSet.halfDeep,
  }
})

const loadingColor = computed<any>(()=>{
  return customComponentThemeProvider.value.colorSet.extension1
})

</script>

<style scoped>
.component-root {
  position: absolute;
  top: 0px;
  width: 100%;
  min-width: 120px;
  background-color: #2e3440;
  height: calc(100% - 30px);
  overflow: scroll;
  left: 50%;
  transform: translateX(-50%);
}

.component-root::-webkit-scrollbar {
  display: none;
}

.list-area-mask {
  position: absolute;
  width: 100%;
  height: 100%;
}

.loading-icon {
  position: absolute;
  height: 32px;
  width: 32px;
  top: 50%;
  left: 50%;
  animation: rotation;
  animation-iteration-count: infinite;
  animation-duration: 2s;
}

@keyframes rotation {
  0% {
    transform: translate(-50%,-50%) rotate(0deg);
  }
  100% {
    transform: translate(-50%,-50%) rotate(360deg);
  }
}
</style>