<template>
  <div class="settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>系统设置</h3>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="150px" style="max-width: 600px">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input v-model="basicSettings.systemDescription" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="管理员邮箱">
              <el-input v-model="basicSettings.adminEmail" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="basicSettings.contactPhone" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveBasic">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="预约设置" name="appointment">
          <el-form :model="appointmentSettings" label-width="150px" style="max-width: 600px">
            <el-form-item label="预约提前天数">
              <el-input-number v-model="appointmentSettings.advanceDays" :min="1" :max="30" />
              <span class="form-tip">用户可以提前多少天预约</span>
            </el-form-item>
            <el-form-item label="每天最大预约数">
              <el-input-number v-model="appointmentSettings.maxDailyAppointments" :min="1" :max="1000" />
              <span class="form-tip">每天最多允许的预约数量</span>
            </el-form-item>
            <el-form-item label="预约开始时间">
              <el-time-picker
                v-model="appointmentSettings.startTime"
                placeholder="选择开始时间"
                format="HH:mm"
                value-format="HH:mm"
              />
            </el-form-item>
            <el-form-item label="预约结束时间">
              <el-time-picker
                v-model="appointmentSettings.endTime"
                placeholder="选择结束时间"
                format="HH:mm"
                value-format="HH:mm"
              />
            </el-form-item>
            <el-form-item label="启用预约功能">
              <el-switch v-model="appointmentSettings.enabled" :active-value="1" :inactive-value="0" />
              <span class="form-tip">是否开放预约功能</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveAppointment">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="提醒设置" name="reminder">
          <el-form :model="reminderSettings" label-width="150px" style="max-width: 600px">
            <el-form-item label="预约提醒">
              <el-switch v-model="reminderSettings.appointmentReminder" :active-value="1" :inactive-value="0" />
              <span class="form-tip">年龄到期发送提醒</span>
            </el-form-item>
            <el-form-item label="接种提醒">
              <el-switch v-model="reminderSettings.vaccinationReminder" :active-value="1" :inactive-value="0" />
              <span class="form-tip">接种前一天发送提醒</span>
            </el-form-item>
            <el-form-item label="短信提醒">
              <el-switch v-model="reminderSettings.smsReminder" :active-value="1" :inactive-value="0" />
              <span class="form-tip">通过短信发送提醒</span>
            </el-form-item>
            <el-form-item label="提醒时间">
              <el-time-picker
                v-model="reminderSettings.reminderTime"
                placeholder="选择提醒时间"
                format="HH:mm"
                value-format="HH:mm"
              />
            </el-form-item>
            <el-form-item label="提前提醒天数">
              <el-input v-model="reminderSettings.reminderDaysBefore" placeholder="例如：3,1" />
              <span class="form-tip">提前几天提醒（多个天数用逗号分隔）</span>
            </el-form-item>
            <el-form-item label="启用提醒功能">
              <el-switch v-model="reminderSettings.enabled" :active-value="1" :inactive-value="0" />
              <span class="form-tip">是否启用提醒功能</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveReminder">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="库存预警" name="stock">
          <el-form :model="stockSettings" label-width="150px" style="max-width: 600px">
            <el-form-item label="低库存阈值">
              <el-input-number v-model="stockSettings.lowStockThreshold" :min="0" :max="100" />
              <span class="form-tip">低于此数量时显示库存紧张</span>
            </el-form-item>
            <el-form-item label="缺货阈值">
              <el-input-number v-model="stockSettings.outOfStockThreshold" :min="0" :max="50" />
              <span class="form-tip">低于此数量时显示库存缺货</span>
            </el-form-item>
            <el-form-item label="预警通知">
              <el-switch v-model="stockSettings.stockAlert" />
              <span class="form-tip">库存低于阈值时发送通知</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveStock">保存</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getBasicSettings, 
  updateBasicSettings,
  getAppointmentSettings, 
  updateAppointmentSettings,
  getReminderSettings, 
  updateReminderSettings 
} from '@/api/settings'

const activeTab = ref('basic')

const basicSettings = reactive({
  systemName: '社区儿童疫苗接种管理系统',
  systemDescription: '为社区儿童提供疫苗接种预约和管理的平台',
  adminEmail: '',
  contactPhone: ''
})

const appointmentSettings = reactive({
  advanceDays: 7,
  maxDailyAppointments: 100,
  startTime: '08:00',
  endTime: '17:00',
  enabled: 1
})

const reminderSettings = reactive({
  appointmentReminder: 1,
  vaccinationReminder: 1,
  smsReminder: 0,
  reminderTime: '09:00',
  reminderDaysBefore: '3,1',
  enabled: 1
})

const stockSettings = reactive({
  lowStockThreshold: 20,
  outOfStockThreshold: 10,
  stockAlert: true
})

const loadSettings = async () => {
  try {
    // 从后端加载基本设置
    const basicRes = await getBasicSettings()
    if (basicRes.data) {
      Object.assign(basicSettings, basicRes.data)
    }
    
    // 从后端加载预约设置
    const appointmentRes = await getAppointmentSettings()
    if (appointmentRes.data) {
      Object.assign(appointmentSettings, appointmentRes.data)
    }
    
    // 从后端加载提醒设置
    const reminderRes = await getReminderSettings()
    if (reminderRes.data) {
      Object.assign(reminderSettings, reminderRes.data)
    }
    
    // 库存预警设置使用localStorage
    const savedSettings = localStorage.getItem('systemSettings')
    if (savedSettings) {
      const settings = JSON.parse(savedSettings)
      Object.assign(stockSettings, settings.stock || {})
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

const handleSaveBasic = async () => {
  try {
    await updateBasicSettings(basicSettings)
    ElMessage.success('基本设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  }
}

const handleSaveAppointment = async () => {
  try {
    await updateAppointmentSettings(appointmentSettings)
    ElMessage.success('预约设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  }
}

const handleSaveReminder = async () => {
  try {
    await updateReminderSettings(reminderSettings)
    ElMessage.success('提醒设置保存成功')
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
  }
}

const handleSaveStock = () => {
  // 库存预警设置仍使用localStorage（暂时）
  ElMessage.success('库存预警设置保存成功')
  saveToLocalStorage()
}

const saveToLocalStorage = () => {
  const settings = {
    basic: basicSettings,
    appointment: appointmentSettings,
    reminder: reminderSettings,
    stock: stockSettings
  }
  localStorage.setItem('systemSettings', JSON.stringify(settings))
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.settings {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}
</style>