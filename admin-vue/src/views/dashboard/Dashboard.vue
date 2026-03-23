<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="dashboard-title">数据看板</h1>
      <p class="dashboard-subtitle">实时监控疫苗接种管理系统</p>
    </div>

    <el-row :gutter="24" class="stats-cards">
      <el-col :span="6">
        <div class="stat-card user-card">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.userCount || 0 }}</div>
            <div class="stat-label">用户总数</div>
            <div class="stat-trend">
              <span v-if="statistics.userGrowthRate > 0" class="trend-up">↑ {{ statistics.userGrowthRate }}%</span>
              <span v-else-if="statistics.userGrowthRate < 0" class="trend-down">↓ {{ Math.abs(statistics.userGrowthRate) }}%</span>
              <span v-else class="trend-stable">→ 持平</span>
              <span class="trend-text">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card child-card">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.childCount || 0 }}</div>
            <div class="stat-label">儿童总数</div>
            <div class="stat-trend">
              <span v-if="statistics.childGrowthRate > 0" class="trend-up">↑ {{ statistics.childGrowthRate }}%</span>
              <span v-else-if="statistics.childGrowthRate < 0" class="trend-down">↓ {{ Math.abs(statistics.childGrowthRate) }}%</span>
              <span v-else class="trend-stable">→ 持平</span>
              <span class="trend-text">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card vaccine-card">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><FirstAidKit /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.vaccineCount || 0 }}</div>
            <div class="stat-label">疫苗总数</div>
            <div class="stat-trend">
              <span v-if="statistics.vaccineGrowthRate > 0" class="trend-up">↑ {{ statistics.vaccineGrowthRate }}%</span>
              <span v-else-if="statistics.vaccineGrowthRate < 0" class="trend-down">↓ {{ Math.abs(statistics.vaccineGrowthRate) }}%</span>
              <span v-else class="trend-stable">→ 持平</span>
              <span class="trend-text">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card appointment-card">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.appointmentCount || 0 }}</div>
            <div class="stat-label">预约总数</div>
            <div class="stat-trend">
              <span v-if="statistics.appointmentGrowthRate > 0" class="trend-up">↑ {{ statistics.appointmentGrowthRate }}%</span>
              <span v-else-if="statistics.appointmentGrowthRate < 0" class="trend-down">↓ {{ Math.abs(statistics.appointmentGrowthRate) }}%</span>
              <span v-else class="trend-stable">→ 持平</span>
              <span class="trend-text">较上月</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="chart-section">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <h3>今日预约统计</h3>
              <el-tag size="small">实时</el-tag>
            </div>
          </template>
          <div class="chart-content">
            <div class="stat-bar">
              <div class="bar-item">
                <div class="bar-label">待审核</div>
                <div class="bar-value">{{ statistics.pendingAppointments || 0 }}</div>
                <div class="bar-progress">
                  <div class="bar-fill pending" :style="{ width: getPercentage(statistics.pendingAppointments) + '%' }"></div>
                </div>
              </div>
              <div class="bar-item">
                <div class="bar-label">已通过</div>
                <div class="bar-value">{{ statistics.approvedAppointments || 0 }}</div>
                <div class="bar-progress">
                  <div class="bar-fill approved" :style="{ width: getPercentage(statistics.approvedAppointments) + '%' }"></div>
                </div>
              </div>
              <div class="bar-item">
                <div class="bar-label">已拒绝</div>
                <div class="bar-value">{{ statistics.rejectedAppointments || 0 }}</div>
                <div class="bar-progress">
                  <div class="bar-fill rejected" :style="{ width: getPercentage(statistics.rejectedAppointments) + '%' }"></div>
                </div>
              </div>
              <div class="bar-item">
                <div class="bar-label">已接种</div>
                <div class="bar-value">{{ statistics.completedAppointments || 0 }}</div>
                <div class="bar-progress">
                  <div class="bar-fill completed" :style="{ width: getPercentage(statistics.completedAppointments) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <h3>疫苗库存统计</h3>
              <el-tag size="small">实时</el-tag>
            </div>
          </template>
          <div class="chart-content">
            <div class="stock-cards">
              <div class="stock-card sufficient">
                <div class="stock-icon">✓</div>
                <div class="stock-info">
                  <div class="stock-value">{{ statistics.sufficientStock || 0 }}</div>
                  <div class="stock-label">库存充足</div>
                </div>
              </div>
              <div class="stock-card low">
                <div class="stock-icon">!</div>
                <div class="stock-info">
                  <div class="stock-value">{{ statistics.lowStock || 0 }}</div>
                  <div class="stock-label">库存紧张</div>
                </div>
              </div>
              <div class="stock-card out">
                <div class="stock-icon">×</div>
                <div class="stock-info">
                  <div class="stock-value">{{ statistics.outOfStock || 0 }}</div>
                  <div class="stock-label">库存缺货</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, UserFilled, FirstAidKit, Calendar } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getParentCount,getChildCount,getVaccineCount,getAppointmentCount,getApprovedCount,getRejectedCount,getreviewCount, getCompletedCount, getStockWarning } from '../../api/statistics'

const statistics = ref({
  userCount: 0,
  childCount: 0,
  vaccineCount: 0,
  appointmentCount: 0,
  userGrowthRate: 12.5,
  childGrowthRate: 8.3,
  vaccineGrowthRate: 5.2,
  appointmentGrowthRate: 15.7,
  pendingAppointments: 0,
  approvedAppointments: 0,
  rejectedAppointments: 0,
  completedAppointments:0,
  sufficientStock: 0,
  lowStock: 0,
  outOfStock: 0
})

const getPercentage = (value) => {
  const total = (statistics.value.pendingAppointments) +
                (statistics.value.approvedAppointments) +
                (statistics.value.rejectedAppointments)+
                (statistics.value.completedAppointments)
  if (total === 0) return 0
  return Math.round((value / total) * 100)
}

const loadStatistics = async () => {

  try {
    //获取库存预警配置
    const savedSettings =  localStorage.getItem("systemSettings")
    let lowStockThreshold = 20
    let outOfStockThreshold = 10
    if(savedSettings){
      const settings = JSON.parse(savedSettings)
      lowStockThreshold = settings.stock?. lowStockThreshold || 20
      outOfStockThreshold = settings.stock?. outOfStockThreshold || 10
    }

    // 获取基础统计数据

    const [userRes, childRes, vaccineRes, appointmentRes] = await Promise.all([

      getParentCount(),

     getChildCount(),

      getVaccineCount(),

      getAppointmentCount(),
    

    ])

    // 获取预约状态统计

    const [pendingRes, approvedRes, rejectedRes,completedRes] = await Promise.all([

      getreviewCount(),

      getApprovedCount(),

      getRejectedCount(),
      getCompletedCount()

    ])
        //疫苗预警
    const stockRes = await getStockWarning(lowStockThreshold,outOfStockThreshold)

    statistics.value.userCount = userRes.data || 0
    statistics.value.childCount = childRes.data || 0
    statistics.value.vaccineCount = vaccineRes.data || 0
    statistics.value.appointmentCount = appointmentRes.data || 0
    statistics.value.completedAppointments = completedRes.data || 0
    statistics.value.pendingAppointments = pendingRes.data || 0
    statistics.value.approvedAppointments = approvedRes.data || 0
    statistics.value.rejectedAppointments = rejectedRes.data || 0
    statistics.value.sufficientStock = stockRes.data.sufficientStock || 0
    statistics.value.lowStock = stockRes.data.lowStock || 0
    statistics.value.outOfStock = stockRes.data.outOfStock || 0


    // // 设置疫苗库存统计（使用后端返回的数据）

    // if (stockStatsRes.data) {

    //   statistics.value.sufficientStock = stockStatsRes.data.sufficientStock || 0

    //   statistics.value.lowStock = stockStatsRes.data.lowStock || 0

    //   statistics.value.outOfStock = stockStatsRes.data.outOfStock || 0

    // }

   

  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.dashboard-header {
  padding: 40px 40px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-bottom: 30px;
}

.dashboard-title {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
  letter-spacing: 1px;
}

.dashboard-subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
  font-weight: 300;
}

.stats-cards {
  padding: 0 40px;
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 16px;
  padding: 24px;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.user-card::before {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.child-card::before {
  background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
}

.vaccine-card::before {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
}

.appointment-card::before {
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.user-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.child-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.vaccine-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.appointment-card .stat-icon-wrapper {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-icon {
  font-size: 32px;
  color: white;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  line-height: 1.2;
}

.stat-label {
  font-size: 15px;
  color: #666;
  font-weight: 500;
  margin-bottom: 12px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.trend-up {
  color: #67c23a;
  font-weight: 600;
}

.trend-stable {
  color: #909399;
  font-weight: 600;
}

.trend-down {
  color: #f56c6c;
  font-weight: 600;
}

.trend-text {
  color: #999;
}

.chart-section {
  padding: 0 40px 40px;
}

.chart-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.chart-content {
  padding: 20px 0;
}

.stat-bar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.bar-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.bar-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.bar-progress {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1s ease-in-out;
}

.bar-fill.pending {
  background: linear-gradient(90deg, #ffd700 0%, #ffed4e 100%);
}

.bar-fill.approved {
  background: linear-gradient(90deg, #67c23a 0%, #95d475 100%);
}

.bar-fill.rejected {
  background: linear-gradient(90deg, #f56c6c 0%, #f89898 100%);
}
.bar-fill.completed {
  background: linear-gradient(90deg, #409eff 0%, #69b1ff 100%);
}

.stock-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stock-card {
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stock-card:hover {
  transform: translateY(-4px);
}

.stock-card.sufficient {
  background: linear-gradient(135deg, #d4fc79 0%, #96e6a1 100%);
}

.stock-card.low {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.stock-card.out {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.stock-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  flex-shrink: 0;
}

.stock-info {
  flex: 1;
}

.stock-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stock-label {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}
</style>