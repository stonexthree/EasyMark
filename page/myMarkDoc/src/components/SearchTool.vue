<template>
  <n-modal class="component-root" v-model:show="searchStatus.showSearchTool" :z-index=zIndex>
    <n-card class="content"
            :bordered="false"
            size="huge"
            role="dialog"
            aria-modal="true"
            content-style="padding:10px"
    >
      <n-input v-model:value="keyword" type="text" size="large" placeholder="输入 文档/标签 名称进行搜索"
               @keyup="valueInput"/>
      <n-list :show-divider=false hoverable clickable class="list">
        <n-list-item style="margin-top: 10px" v-show="docNameSearchShow" @click="searchHandel('name',keyword)">
          <div>
            <n-ellipsis>
              搜索文档名称：{{ keyword }}
            </n-ellipsis>
          </div>
        </n-list-item>
        <n-list-item style="margin-top: 10px" v-for="tag in tags" @click="searchHandel('label',tag)">
          <div>
            <n-ellipsis>
              搜索标签：{{ tag }}
            </n-ellipsis>
          </div>
        </n-list-item>
      </n-list>
    </n-card>
  </n-modal>
</template>

<script lang="ts">
export default {
  name: "SearchTool"
}
</script>
<script setup lang="ts">
import {defineProps, ref, Ref, computed} from 'vue'
import {NModal, NCard, NInput, NList, NListItem, NEllipsis} from 'naive-ui'
import axios from 'axios'
import {TagApi, SearchApi} from '../api-define'
import {loginStatus, searchStatus} from '../globalStatus'
import {DocInfo, SearchScope} from "../model/models";
import {useRouter} from 'vue-router';

const router = useRouter();
const keyword: Ref<string> = ref('');
const tags: Ref<string[]> = ref([])
const docNameSearchShow = computed<boolean>(() => {
  return keyword.value !== '';
})

function valueInput(event: KeyboardEvent) {
  tags.value = []
  if (keyword.value === '') {
    return;
  }
  if (event.key === 'Enter') {
    searchHandel('composite', keyword.value);
    return;
  }
  axios.request(TagApi.searchTag(keyword.value)).then((response) => {
    if (response.data.code === '00000') {
      tags.value = response.data.data;
      return;
    }
  })
}

document.onkeydown = (event) => {
  if ((event.metaKey && event.key === 'k') || (event.ctrlKey && event.key === 'k')) {
    searchStatus.value.showSearchTool = true;
  }
}

function searchHandel(scope: SearchScope, kw: string): void {
  router.push({name: 'searchResult'});
  searchStatus.value.searchApi = SearchApi.searchDoc(scope, kw);
  searchStatus.value.showSearchTool = false;
}

/////////////////////
//样式部分
const zIndex: Ref<any> = ref(2);
</script>

<style scoped>
.content {
  width: 600px;
  max-height: 400px;
  overflow-y: scroll;
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  top: 80px;
}
.content::-webkit-scrollbar{
  display: none;
}
</style>