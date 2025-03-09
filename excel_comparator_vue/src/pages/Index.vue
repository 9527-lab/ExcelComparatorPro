<script setup>
import {ref} from 'vue';
import {message, Upload, Button, Checkbox, Spin, Table} from 'ant-design-vue';
import {InboxOutlined} from '@ant-design/icons-vue';
import {upload} from "@/api/FileApi";
import {compare} from "@/api/CompareApi";

const fileList1 = ref([]);
const fileList2 = ref([]);
const bomLevels = ref([]);
const results = ref([])
const showResult = ref(false)
const loading = ref(false);
let file1Md5 = ''
let file2Md5 = ''
let matchResultFilename = ''

const title1 = ref('')
const title2 = ref('')

const beforeUpload = (file, type) => {
  const isExcel = file.type.includes('excel') || file.name.endsWith('.xls') || file.name.endsWith('.xlsx');
  if (!isExcel) {
    message.error('只能上传 Excel 文件 (.xls, .xlsx)');
    return Upload.LIST_IGNORE;
  }
  if (type === 0) {
    file1Md5 = ''
    fileList1.value = [file];
  } else {
    file2Md5 = ''
    fileList2.value = [file];
  }
  return false;
};

const startComparison = async () => {
  if (!fileList1.value.length || !fileList2.value.length) {
    return message.warning('请上传两个 Excel 文件');
  }
  if (!bomLevels.value.length) {
    return message.warning('请至少选择一个 BOM 层级');
  }
  if (!file1Md5) {
    let result = await upload(fileList1.value[0])
    if (result.success) {
      file1Md5 = result.data.filename
    } else {
      message.error(result.message);
      return
    }
  }
  if (!file2Md5) {
    let result = await upload(fileList2.value[0])
    if (result.success) {
      file2Md5 = result.data.filename
    } else {
      message.error(result.message);
      return
    }
  }
  if (file1Md5 === file2Md5) {
    message.error('上传的两个文件一致');
    return
  }
  matchResultFilename = ''
  results.value = []
  loading.value = true;
  compare({
    file1Md5,
    file2Md5,
    levels: bomLevels.value
  }).then(res => {
    if (res.success) {
      showResult.value = true
      results.value = res.data.rows
      matchResultFilename = res.data.matchResultFilename || ''
      title1.value = res.data.title1
      title2.value = res.data.title2
      message.success('比对完成！');
    } else {
      message.error(res.message);
    }
  }).finally(() => {
    setTimeout(() => {
      loading.value = false;
    }, 1000);
  })
};

const exportResult = () => {
  if (matchResultFilename){
    window.open('/api/file/' + matchResultFilename)
    // window.open(process.env.VUE_APP_BASE_API + '/file/' + matchResultFilename)
    message.success('导出成功！');
  }else {
    message.error('无可导出的匹配结果')
  }
};
</script>

<template>
  <Spin :spinning="loading" size="large" fullscreen class="fullscreen-loading">
    <div class="container">
      <div class="header">
        <svg t="1741508039626" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
             p-id="1556" width="32" height="32">
          <path
              d="M145.6 0C100.8 0 64 36.8 64 81.6v860.8C64 987.2 100.8 1024 145.6 1024h732.8c44.8 0 81.6-36.8 81.6-81.6V324.8L657.6 0h-512z"
              fill="#45B058" p-id="1557"></path>
          <path
              d="M374.4 862.4c-3.2 0-6.4-1.6-8-3.2l-59.2-80-60.8 80c-1.6 1.6-4.8 3.2-8 3.2-6.4 0-11.2-4.8-11.2-11.2 0-1.6 0-4.8 1.6-6.4l62.4-81.6-57.6-78.4c-1.6-1.6-3.2-3.2-3.2-6.4 0-4.8 4.8-11.2 11.2-11.2 4.8 0 8 1.6 9.6 4.8l56 73.6 54.4-73.6c1.6-3.2 4.8-4.8 8-4.8 6.4 0 12.8 4.8 12.8 11.2 0 3.2-1.6 4.8-1.6 6.4l-59.2 76.8 62.4 83.2c1.6 1.6 3.2 4.8 3.2 6.4 0 6.4-6.4 11.2-12.8 11.2z m160-1.6H448c-9.6 0-17.6-8-17.6-17.6V678.4c0-6.4 4.8-11.2 12.8-11.2 6.4 0 11.2 4.8 11.2 11.2v161.6h80c6.4 0 11.2 4.8 11.2 9.6 0 6.4-4.8 11.2-11.2 11.2z m112 3.2c-28.8 0-51.2-9.6-67.2-24-3.2-1.6-3.2-4.8-3.2-8 0-6.4 3.2-12.8 11.2-12.8 1.6 0 4.8 1.6 6.4 3.2 12.8 11.2 32 20.8 54.4 20.8 33.6 0 44.8-19.2 44.8-33.6 0-49.6-113.6-22.4-113.6-89.6 0-32 27.2-54.4 65.6-54.4 24 0 46.4 8 60.8 20.8 3.2 1.6 4.8 4.8 4.8 8 0 6.4-4.8 12.8-11.2 12.8-1.6 0-4.8-1.6-6.4-3.2-14.4-11.2-32-16-49.6-16-24 0-40 11.2-40 30.4 0 43.2 113.6 17.6 113.6 89.6 0 27.2-19.2 56-70.4 56z"
              fill="#FFFFFF" p-id="1558"></path>
          <path d="M960 326.4v16H755.2s-102.4-20.8-99.2-108.8c0 0 3.2 92.8 96 92.8h208z" fill="#349C42"
                p-id="1559"></path>
          <path d="M656 0v233.6c0 25.6 19.2 92.8 99.2 92.8H960L656 0z" fill="#FFFFFF" p-id="1560"></path>
        </svg>
        <span>EXCEL COMPARATOR</span>
      </div>
      <div>
        <div class="upload-section">
          <Upload.Dragger :before-upload="file => beforeUpload(file, 0)" accept=".xlsx,.xls"
                          :file-list="fileList1" :maxCount="1" :showUploadList="false">
            <p class="ant-upload-drag-icon">
              <InboxOutlined/>
            </p>
            <p class="ant-upload-text">单击或拖动文件到此区域进行上传</p>
            <p class="ant-upload-hint">
              请上传xlsx或xls文件
            </p>
            <div class="file" v-if="fileList1.length > 0">
              <svg t="1741508222510" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="1715" width="32" height="32">
                <path
                    d="M629.312 128L864 358.4v499.2c0 23.04-15.616 38.4-39.104 38.4H277.312c-23.424 0-39.04-15.36-39.04-38.4v-38.4c0-23.04 15.616-38.4 39.04-38.4 23.488 0 39.104 15.36 39.104 38.4h469.376V396.8H629.312c-23.424 0-39.04-15.36-39.04-38.4V204.8H316.352v230.4c0 23.04-15.616 38.4-39.04 38.4-23.488 0-39.168-15.36-39.168-38.4V166.4c0-23.04 15.68-38.4 39.104-38.4h352z"
                    fill="#000000" fill-opacity=".88" p-id="1716"></path>
                <path
                    d="M668.416 512c23.488 0 39.168 15.36 39.168 38.4V704c0 23.04-15.68 38.4-39.168 38.4H199.104c-23.488 0-39.104-15.36-39.104-38.4V550.4c0-23.04 15.616-38.4 39.104-38.4h469.312z m-113.408 57.6c-11.712 0-19.52 0-27.392 3.84-7.808 3.84-11.712 7.68-15.616 11.52s-3.904 11.52-3.904 15.36c0 7.68 3.904 15.36 11.712 23.04 3.904 3.84 15.68 7.68 27.392 11.52h7.808c7.808 3.84 15.68 3.84 19.584 7.68 3.84 3.84 3.84 3.84 3.84 7.68s0 3.84-3.84 7.68c-3.904 3.84-7.808 3.84-11.776 3.84-7.808 0-11.712-3.84-15.616-7.68 0-3.84-3.904-3.84-3.904-7.68v-3.84l-35.2 3.84c0 11.52 3.904 19.2 11.712 26.88s19.584 11.52 39.104 11.52c11.776 0 19.584 0 27.392-3.84a30.272 30.272 0 0 0 15.68-15.36c3.84-7.68 7.808-11.52 7.808-19.2s0-11.52-3.904-15.36a53.888 53.888 0 0 0-15.68-11.52c-3.84-3.84-15.616-3.84-27.392-7.68h-7.808c-7.808 0-11.712-3.84-11.712-3.84l-3.904-3.84c0-3.84 0-3.84 3.84-7.68 3.968 0 3.968-3.84 7.872-3.84s7.808 0 11.712 3.84c3.968 0 3.968 3.84 3.968 7.68v3.84h31.232c0-11.52-7.808-19.2-11.712-26.88-7.808-7.68-15.616-11.52-31.296-11.52z m-246.4 0h-39.04l35.2 53.76-39.168 57.6h39.104l23.488-34.56 23.424 34.56h39.168l-39.168-57.6 35.2-53.76h-39.04l-19.584 34.56-19.584-34.56z m129.088 0h-35.2v111.36h89.92v-26.88H437.76V569.6z"
                    fill="#44B058" p-id="1717"></path>
              </svg>
              <span class="filename">{{ fileList1[0].name }}</span>
            </div>
          </Upload.Dragger>
          <Upload.Dragger :before-upload="file => beforeUpload(file, 1)" accept=".xlsx,.xls"
                          :file-list="fileList2" :maxCount="1" :showUploadList="false">
            <p class="ant-upload-drag-icon">
              <InboxOutlined/>
            </p>
            <p class="ant-upload-text">单击或拖动文件到此区域进行上传</p>
            <p class="ant-upload-hint">
              请上传xlsx或xls文件
            </p>
            <div class="file" v-if="fileList2.length > 0">
              <svg t="1741508222510" class="icon" viewBox="0 0 1024 1024" version="1.1"
                   xmlns="http://www.w3.org/2000/svg" p-id="1715" width="32" height="32">
                <path
                    d="M629.312 128L864 358.4v499.2c0 23.04-15.616 38.4-39.104 38.4H277.312c-23.424 0-39.04-15.36-39.04-38.4v-38.4c0-23.04 15.616-38.4 39.04-38.4 23.488 0 39.104 15.36 39.104 38.4h469.376V396.8H629.312c-23.424 0-39.04-15.36-39.04-38.4V204.8H316.352v230.4c0 23.04-15.616 38.4-39.04 38.4-23.488 0-39.168-15.36-39.168-38.4V166.4c0-23.04 15.68-38.4 39.104-38.4h352z"
                    fill="#000000" fill-opacity=".88" p-id="1716"></path>
                <path
                    d="M668.416 512c23.488 0 39.168 15.36 39.168 38.4V704c0 23.04-15.68 38.4-39.168 38.4H199.104c-23.488 0-39.104-15.36-39.104-38.4V550.4c0-23.04 15.616-38.4 39.104-38.4h469.312z m-113.408 57.6c-11.712 0-19.52 0-27.392 3.84-7.808 3.84-11.712 7.68-15.616 11.52s-3.904 11.52-3.904 15.36c0 7.68 3.904 15.36 11.712 23.04 3.904 3.84 15.68 7.68 27.392 11.52h7.808c7.808 3.84 15.68 3.84 19.584 7.68 3.84 3.84 3.84 3.84 3.84 7.68s0 3.84-3.84 7.68c-3.904 3.84-7.808 3.84-11.776 3.84-7.808 0-11.712-3.84-15.616-7.68 0-3.84-3.904-3.84-3.904-7.68v-3.84l-35.2 3.84c0 11.52 3.904 19.2 11.712 26.88s19.584 11.52 39.104 11.52c11.776 0 19.584 0 27.392-3.84a30.272 30.272 0 0 0 15.68-15.36c3.84-7.68 7.808-11.52 7.808-19.2s0-11.52-3.904-15.36a53.888 53.888 0 0 0-15.68-11.52c-3.84-3.84-15.616-3.84-27.392-7.68h-7.808c-7.808 0-11.712-3.84-11.712-3.84l-3.904-3.84c0-3.84 0-3.84 3.84-7.68 3.968 0 3.968-3.84 7.872-3.84s7.808 0 11.712 3.84c3.968 0 3.968 3.84 3.968 7.68v3.84h31.232c0-11.52-7.808-19.2-11.712-26.88-7.808-7.68-15.616-11.52-31.296-11.52z m-246.4 0h-39.04l35.2 53.76-39.168 57.6h39.104l23.488-34.56 23.424 34.56h39.168l-39.168-57.6 35.2-53.76h-39.04l-19.584 34.56-19.584-34.56z m129.088 0h-35.2v111.36h89.92v-26.88H437.76V569.6z"
                    fill="#44B058" p-id="1717"></path>
              </svg>
              <span class="filename">{{ fileList2[0].name }}</span>
            </div>
          </Upload.Dragger>
        </div>

        <div class="bom-section">
          <span style="margin-right: 10px">BOM层级</span>
          <Checkbox.Group v-model:value="bomLevels" :options="['0', '1', '2', '3', '4', '5']"/>
        </div>

        <div class="button-section">
          <Button type="primary" block @click="startComparison">开始对比</Button>
          <Button type="default" block @click="exportResult">导出结果</Button>
        </div>

        <div class="table-section" v-if="showResult">
          <Table :columns="[
                            {
                              title: '子项物料编码',
                              dataIndex: 'id',
                              key: 'id',
                            },
                            {
                              title: title1,
                              dataIndex: 'quantity1',
                              key: 'quantity1',
                            },
                            {
                              title: title2,
                              dataIndex: 'quantity2',
                              key: 'quantity2',
                            },
                         ]"
                 :data-source="results"
                 row-key="id"
          />
        </div>

      </div>
    </div>
  </Spin>
</template>

<style scoped lang="scss">

.container {
  width: 100%;
  min-height: 100vh;
  //overflow: hidden;
  margin: 0;
  background: #F5F5F5;

  .header {
    width: 100%;
    height: 65px;
    background: #fff;
    box-sizing: border-box;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    border-bottom: 1px solid #ccc;

    > span {
      margin-left: 10px;
    }
  }

  .upload-section {
    display: flex;
    align-items: flex-start;
    justify-content: center;
    margin-top: 20px;

    .ant-upload-wrapper {
      width: 400px;
      padding: 10px;

      .file {
        padding: 10px;
        display: flex;
        align-items: center;
        justify-content: flex-start;

        > .filename {
          white-space: nowrap;
          text-overflow: ellipsis;
          width: 300px;
          overflow: hidden;
          text-align: left;
        }
      }
    }
  }

  .bom-section {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 20px auto;
  }

  .button-section {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    width: 400px;
    margin: 0 auto;
    gap: 10px;
  }

  .table-section {
    width: 900px;
    margin: 20px auto;
  }
}

</style>
