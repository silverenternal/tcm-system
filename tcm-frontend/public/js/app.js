// 定义全局API基础URL
window.API_BASE_URL = 'http://localhost:58080';

// API服务
const ApiService = {
  get(url) {
    return axios.get(`${window.API_BASE_URL}${url}`);
  },
  post(url, data) {
    return axios.post(`${window.API_BASE_URL}${url}`, data);
  },
  put(url, data) {
    return axios.put(`${window.API_BASE_URL}${url}`, data);
  },
  delete(url) {
    return axios.delete(`${window.API_BASE_URL}${url}`);
  }
};

// 患者列表组件
const PatientList = {
  template: `
    <div>
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="mb-0">患者列表</h5>
          <button class="btn btn-primary" @click="showAddForm = true">新增患者</button>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>姓名</th>
                  <th>性别</th>
                  <th>年龄</th>
                  <th>身份证</th>
                  <th>电话</th>
                  <th>地址</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="patient in patients" :key="patient.id">
                  <td>{{ patient.id }}</td>
                  <td>{{ patient.name }}</td>
                  <td>{{ patient.gender === 1 ? '男' : patient.gender === 0 ? '女' : '其他' }}</td>
                  <td>{{ patient.age }}</td>
                  <td>{{ patient.idCard }}</td>
                  <td>{{ patient.phone }}</td>
                  <td>{{ patient.address }}</td>
                  <td>
                    <button class="btn btn-sm btn-info" @click="editPatient(patient)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deletePatient(patient.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 患者表单模态框 -->
      <div class="modal fade" :class="{ show: showAddForm }" :style="{ display: showAddForm ? 'block' : 'none' }" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ editingPatient ? '编辑患者' : '新增患者' }}</h5>
              <button type="button" class="close" @click="closeForm">
                <span>&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <patient-form :patient="editingPatient" @save="savePatient" @cancel="closeForm"></patient-form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      patients: [],
      showAddForm: false,
      editingPatient: null
    };
  },
  created() {
    this.loadPatients();
  },
  methods: {
    loadPatients() {
      ApiService.get('/api/patients')
        .then(response => {
          this.patients = response.data;
        })
        .catch(error => {
          console.error('获取患者列表失败:', error);
        });
    },
    editPatient(patient) {
      this.editingPatient = { ...patient };
      this.showAddForm = true;
    },
    deletePatient(id) {
      if (confirm('确定要删除这个患者吗？')) {
        ApiService.delete(`/api/patients/${id}`)
          .then(() => {
            this.loadPatients();
          })
          .catch(error => {
            console.error('删除患者失败:', error);
          });
      }
    },
    savePatient(patientData) {
      let request;
      if (patientData.id) {
        // 更新现有患者
        request = ApiService.put(`/api/patients/${patientData.id}`, patientData);
      } else {
        // 创建新患者
        request = ApiService.post('/api/patients', patientData);
      }

      request.then(() => {
        this.closeForm();
        this.loadPatients();
      })
      .catch(error => {
        console.error('保存患者失败:', error);
      });
    },
    closeForm() {
      this.showAddForm = false;
      this.editingPatient = null;
    }
  },
  components: {
    'patient-form': {
      props: ['patient'],
      template: `
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label for="name">姓名 *</label>
            <input type="text" class="form-control" id="name" v-model="formData.name" required>
          </div>
          <div class="form-row">
            <div class="form-group col-md-6">
              <label for="gender">性别</label>
              <select class="form-control" id="gender" v-model="formData.gender">
                <option value="">请选择</option>
                <option value="0">女</option>
                <option value="1">男</option>
                <option value="2">其他</option>
              </select>
            </div>
            <div class="form-group col-md-6">
              <label for="age">年龄</label>
              <input type="number" class="form-control" id="age" v-model="formData.age">
            </div>
          </div>
          <div class="form-group">
            <label for="birthDate">出生日期</label>
            <input type="date" class="form-control" id="birthDate" v-model="formData.birthDate">
          </div>
          <div class="form-group">
            <label for="idCard">身份证号</label>
            <input type="text" class="form-control" id="idCard" v-model="formData.idCard" :required="!isSelfDiagnosisMode">
            <small v-if="isSelfDiagnosisMode" class="form-text text-muted">自诊模式下可以留空，系统将自动生成ID</small>
          </div>
          <div class="form-group">
            <label for="phone">电话</label>
            <input type="text" class="form-control" id="phone" v-model="formData.phone">
          </div>
          <div class="form-group">
            <label for="address">地址</label>
            <input type="text" class="form-control" id="address" v-model="formData.address">
          </div>
          <div class="form-group">
            <label for="occupation">职业</label>
            <input type="text" class="form-control" id="occupation" v-model="formData.occupation">
          </div>
          <div class="form-group">
            <label for="maritalStatus">婚姻状况</label>
            <select class="form-control" id="maritalStatus" v-model="formData.maritalStatus">
              <option value="">请选择</option>
              <option value="0">未婚</option>
              <option value="1">已婚</option>
              <option value="2">离异</option>
              <option value="3">丧偶</option>
            </select>
          </div>
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-secondary" @click="$emit('cancel')">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      `,
      data() {
        return {
          formData: {
            name: '',
            gender: null,
            age: null,
            birthDate: '',
            idCard: '',
            phone: '',
            address: '',
            occupation: '',
            maritalStatus: null
          }
        };
      },
      computed: {
        isSelfDiagnosisMode() {
          // 检查是否在自诊组件中使用此表单
          return this.$parent && this.$parent.$options.name === 'SelfDiagnosis';
        }
      },
      created() {
        if (this.patient) {
          this.formData = { ...this.patient };
        }
      },
      watch: {
        patient(newVal) {
          if (newVal) {
            this.formData = { ...newVal };
          } else {
            this.resetForm();
          }
        }
      },
      methods: {
        submitForm() {
          this.$emit('save', { ...this.formData });
        },
        resetForm() {
          this.formData = {
            name: '',
            gender: null,
            age: null,
            birthDate: '',
            idCard: '',
            phone: '',
            address: '',
            occupation: '',
            maritalStatus: null
          };
        }
      }
    }
  }
};

// 医生列表组件
const DoctorList = {
  template: `
    <div>
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="mb-0">医生列表</h5>
          <button class="btn btn-primary" @click="showAddForm = true">新增医生</button>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>姓名</th>
                  <th>科室</th>
                  <th>职称</th>
                  <th>执业资格证号</th>
                  <th>电话</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="doctor in doctors" :key="doctor.id">
                  <td>{{ doctor.id }}</td>
                  <td>{{ doctor.name }}</td>
                  <td>{{ doctor.department }}</td>
                  <td>{{ doctor.title }}</td>
                  <td>{{ doctor.licenseNumber }}</td>
                  <td>{{ doctor.phone }}</td>
                  <td>
                    <span :class="{'badge': true, 'badge-success': doctor.status === 1, 'badge-danger': doctor.status === 0}">
                      {{ doctor.status === 1 ? '在职' : '离职' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-info" @click="editDoctor(doctor)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteDoctor(doctor.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 医生表单模态框 -->
      <div class="modal fade" :class="{ show: showAddForm }" :style="{ display: showAddForm ? 'block' : 'none' }" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ editingDoctor ? '编辑医生' : '新增医生' }}</h5>
              <button type="button" class="close" @click="closeForm">
                <span>&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <doctor-form :doctor="editingDoctor" @save="saveDoctor" @cancel="closeForm"></doctor-form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      doctors: [],
      showAddForm: false,
      editingDoctor: null
    };
  },
  created() {
    this.loadDoctors();
  },
  methods: {
    loadDoctors() {
      ApiService.get('/api/doctors')
        .then(response => {
          this.doctors = response.data;
        })
        .catch(error => {
          console.error('获取医生列表失败:', error);
        });
    },
    editDoctor(doctor) {
      this.editingDoctor = { ...doctor };
      this.showAddForm = true;
    },
    deleteDoctor(id) {
      if (confirm('确定要删除这个医生吗？')) {
        ApiService.delete(`/api/doctors/${id}`)
          .then(() => {
            this.loadDoctors();
          })
          .catch(error => {
            console.error('删除医生失败:', error);
          });
      }
    },
    saveDoctor(doctorData) {
      let request;
      if (doctorData.id) {
        // 更新现有医生
        request = ApiService.put(`/api/doctors/${doctorData.id}`, doctorData);
      } else {
        // 创建新医生
        request = ApiService.post('/api/doctors', doctorData);
      }

      request.then(() => {
        this.closeForm();
        this.loadDoctors();
      })
      .catch(error => {
        console.error('保存医生失败:', error);
      });
    },
    closeForm() {
      this.showAddForm = false;
      this.editingDoctor = null;
    }
  },
  components: {
    'doctor-form': {
      props: ['doctor'],
      template: `
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label for="name">姓名 *</label>
            <input type="text" class="form-control" id="name" v-model="formData.name" required>
          </div>
          <div class="form-group">
            <label for="department">科室</label>
            <input type="text" class="form-control" id="department" v-model="formData.department">
          </div>
          <div class="form-group">
            <label for="title">职称</label>
            <input type="text" class="form-control" id="title" v-model="formData.title">
          </div>
          <div class="form-group">
            <label for="licenseNumber">执业资格证号 *</label>
            <input type="text" class="form-control" id="licenseNumber" v-model="formData.licenseNumber" required>
          </div>
          <div class="form-group">
            <label for="phone">电话</label>
            <input type="text" class="form-control" id="phone" v-model="formData.phone">
          </div>
          <div class="form-group">
            <label for="email">邮箱</label>
            <input type="email" class="form-control" id="email" v-model="formData.email">
          </div>
          <div class="form-group">
            <label for="status">状态</label>
            <select class="form-control" id="status" v-model="formData.status">
              <option value="1">在职</option>
              <option value="0">离职</option>
            </select>
          </div>
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-secondary" @click="$emit('cancel')">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      `,
      data() {
        return {
          formData: {
            name: '',
            department: '',
            title: '',
            licenseNumber: '',
            phone: '',
            email: '',
            status: 1
          }
        };
      },
      created() {
        if (this.doctor) {
          this.formData = { ...this.doctor };
        }
      },
      watch: {
        doctor(newVal) {
          if (newVal) {
            this.formData = { ...newVal };
          } else {
            this.resetForm();
          }
        }
      },
      methods: {
        submitForm() {
          this.$emit('save', { ...this.formData });
        },
        resetForm() {
          this.formData = {
            name: '',
            department: '',
            title: '',
            licenseNumber: '',
            phone: '',
            email: '',
            status: 1
          };
        }
      }
    }
  }
};

// 就诊记录列表组件
const VisitList = {
  template: `
    <div>
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="mb-0">就诊记录列表</h5>
          <button class="btn btn-primary" @click="showAddForm = true">新增就诊记录</button>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>患者</th>
                  <th>医生</th>
                  <th>就诊类型</th>
                  <th>主诉</th>
                  <th>就诊日期</th>
                  <th>中医诊断</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="visit in visits" :key="visit.id">
                  <td>{{ visit.id }}</td>
                  <td>{{ visit.patient ? visit.patient.name : 'N/A' }}</td>
                  <td>{{ visit.doctor ? visit.doctor.name : 'N/A' }}</td>
                  <td>{{ visit.visitType === 0 ? '初诊' : '复诊' }}</td>
                  <td>{{ visit.chiefComplaint | truncate(20) }}</td>
                  <td>{{ visit.visitDate | formatDate }}</td>
                  <td>{{ visit.tcmDiagnosis | truncate(20) }}</td>
                  <td>
                    <button class="btn btn-sm btn-info" @click="editVisit(visit)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteVisit(visit.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 就诊记录表单模态框 -->
      <div class="modal fade" :class="{ show: showAddForm }" :style="{ display: showAddForm ? 'block' : 'none' }" tabindex="-1">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ editingVisit ? '编辑就诊记录' : '新增就诊记录' }}</h5>
              <button type="button" class="close" @click="closeForm">
                <span>&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <visit-form
                :visit="editingVisit"
                :patients="patients"
                :doctors="doctors"
                @save="saveVisit"
                @cancel="closeForm"
              ></visit-form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      visits: [],
      patients: [],
      doctors: [],
      showAddForm: false,
      editingVisit: null
    };
  },
  created() {
    this.loadVisits();
    this.loadPatients();
    this.loadDoctors();
  },
  filters: {
    truncate(value, limit) {
      if (!value) return '';
      value = value.toString();
      if (value.length <= limit) return value;
      return value.substring(0, limit) + '...';
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN');
    }
  },
  methods: {
    loadVisits() {
      ApiService.get('/api/visits')
        .then(response => {
          this.visits = response.data;
        })
        .catch(error => {
          console.error('获取就诊记录失败:', error);
        });
    },
    loadPatients() {
      ApiService.get('/api/patients')
        .then(response => {
          this.patients = response.data;
        })
        .catch(error => {
          console.error('获取患者列表失败:', error);
        });
    },
    loadDoctors() {
      ApiService.get('/api/doctors')
        .then(response => {
          this.doctors = response.data;
        })
        .catch(error => {
          console.error('获取医生列表失败:', error);
        });
    },
    editVisit(visit) {
      this.editingVisit = { ...visit };
      this.showAddForm = true;
    },
    deleteVisit(id) {
      if (confirm('确定要删除这个就诊记录吗？')) {
        ApiService.delete(`/api/visits/${id}`)
          .then(() => {
            this.loadVisits();
          })
          .catch(error => {
            console.error('删除就诊记录失败:', error);
          });
      }
    },
    saveVisit(visitData) {
      let request;
      if (visitData.id) {
        // 更新现有就诊记录
        request = ApiService.put(`/api/visits/${visitData.id}`, visitData);
      } else {
        // 创建新就诊记录
        request = ApiService.post('/api/visits', visitData);
      }

      request.then(() => {
        this.closeForm();
        this.loadVisits();
      })
      .catch(error => {
        console.error('保存就诊记录失败:', error);
      });
    },
    closeForm() {
      this.showAddForm = false;
      this.editingVisit = null;
    }
  },
  components: {
    'visit-form': {
      props: ['visit', 'patients', 'doctors'],
      template: `
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group col-md-6">
              <label for="patientId">患者 *</label>
              <select class="form-control" id="patientId" v-model="formData.patientId" required>
                <option value="">请选择患者</option>
                <option v-for="patient in patients" :value="patient.id" :key="patient.id">
                  {{ patient.name }} ({{ patient.idCard }})
                </option>
              </select>
            </div>
            <div class="form-group col-md-6">
              <label for="doctorId">医生 *</label>
              <select class="form-control" id="doctorId" v-model="formData.doctorId" required>
                <option value="">请选择医生</option>
                <option v-for="doctor in doctors" :value="doctor.id" :key="doctor.id">
                  {{ doctor.name }} ({{ doctor.title }})
                </option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label for="visitType">就诊类型 *</label>
            <select class="form-control" id="visitType" v-model="formData.visitType" required>
              <option value="0">初诊</option>
              <option value="1">复诊</option>
            </select>
          </div>
          <div class="form-group">
            <label for="medicalRecordNumber">病历号</label>
            <input type="text" class="form-control" id="medicalRecordNumber" v-model="formData.medicalRecordNumber">
          </div>
          <div class="form-group">
            <label for="chiefComplaint">主诉</label>
            <textarea class="form-control" id="chiefComplaint" v-model="formData.chiefComplaint" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label for="symptoms">症状</label>
            <textarea class="form-control" id="symptoms" v-model="formData.symptoms" rows="3"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group col-md-6">
              <label for="tongueDiagnosis">舌诊</label>
              <textarea class="form-control" id="tongueDiagnosis" v-model="formData.tongueDiagnosis" rows="2"></textarea>
            </div>
            <div class="form-group col-md-6">
              <label for="pulseDiagnosis">脉诊</label>
              <textarea class="form-control" id="pulseDiagnosis" v-model="formData.pulseDiagnosis" rows="2"></textarea>
            </div>
          </div>
          <div class="form-group">
            <label for="tcmDiagnosis">中医诊断</label>
            <textarea class="form-control" id="tcmDiagnosis" v-model="formData.tcmDiagnosis" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label for="westernDiagnosis">西医诊断</label>
            <textarea class="form-control" id="westernDiagnosis" v-model="formData.westernDiagnosis" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label for="treatmentPlan">治疗方案</label>
            <textarea class="form-control" id="treatmentPlan" v-model="formData.treatmentPlan" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label for="visitDate">就诊日期</label>
            <input type="datetime-local" class="form-control" id="visitDate" v-model="formData.visitDate">
          </div>
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-secondary" @click="$emit('cancel')">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      `,
      data() {
        return {
          formData: {
            patientId: '',
            doctorId: '',
            visitType: 0,
            medicalRecordNumber: '',
            chiefComplaint: '',
            symptoms: '',
            tongueDiagnosis: '',
            pulseDiagnosis: '',
            tcmDiagnosis: '',
            westernDiagnosis: '',
            treatmentPlan: '',
            visitDate: new Date().toISOString().slice(0, 16)
          }
        };
      },
      created() {
        if (this.visit) {
          this.formData = {
            ...this.visit,
            patientId: this.visit.patient ? this.visit.patient.id : '',
            doctorId: this.visit.doctor ? this.visit.doctor.id : '',
            visitDate: this.visit.visitDate ? new Date(this.visit.visitDate).toISOString().slice(0, 16) : new Date().toISOString().slice(0, 16)
          };
        }
      },
      watch: {
        visit(newVal) {
          if (newVal) {
            this.formData = {
              ...newVal,
              patientId: newVal.patient ? newVal.patient.id : '',
              doctorId: newVal.doctor ? newVal.doctor.id : '',
              visitDate: newVal.visitDate ? new Date(newVal.visitDate).toISOString().slice(0, 16) : new Date().toISOString().slice(0, 16)
            };
          } else {
            this.resetForm();
          }
        }
      },
      methods: {
        submitForm() {
          // 转换表单数据以匹配API要求
          const visitData = {
            ...this.formData,
            patient: { id: this.formData.patientId },
            doctor: { id: this.formData.doctorId }
          };
          this.$emit('save', visitData);
        },
        resetForm() {
          this.formData = {
            patientId: '',
            doctorId: '',
            visitType: 0,
            medicalRecordNumber: '',
            chiefComplaint: '',
            symptoms: '',
            tongueDiagnosis: '',
            pulseDiagnosis: '',
            tcmDiagnosis: '',
            westernDiagnosis: '',
            treatmentPlan: '',
            visitDate: new Date().toISOString().slice(0, 16)
          };
        }
      }
    }
  }
};

// 处方列表组件
const PrescriptionList = {
  template: `
    <div>
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="mb-0">处方列表</h5>
          <button class="btn btn-primary" @click="showAddForm = true">新增处方</button>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>就诊</th>
                  <th>医生</th>
                  <th>处方名称</th>
                  <th>治疗天数</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="prescription in prescriptions" :key="prescription.id">
                  <td>{{ prescription.id }}</td>
                  <td>{{ prescription.visit ? prescription.visit.medicalRecordNumber : 'N/A' }}</td>
                  <td>{{ prescription.doctor ? prescription.doctor.name : 'N/A' }}</td>
                  <td>{{ prescription.prescriptionName }}</td>
                  <td>{{ prescription.treatmentDuration }}</td>
                  <td>
                    <span :class="{'badge': true, 'badge-secondary': prescription.status === 0, 'badge-success': prescription.status === 1, 'badge-info': prescription.status === 2}">
                      {{ prescription.status === 0 ? '未取药' : prescription.status === 1 ? '已取药' : '已完成' }}
                    </span>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-info" @click="editPrescription(prescription)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deletePrescription(prescription.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- 处方表单模态框 -->
      <div class="modal fade" :class="{ show: showAddForm }" :style="{ display: showAddForm ? 'block' : 'none' }" tabindex="-1">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ editingPrescription ? '编辑处方' : '新增处方' }}</h5>
              <button type="button" class="close" @click="closeForm">
                <span>&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <prescription-form
                :prescription="editingPrescription"
                :visits="visits"
                :doctors="doctors"
                @save="savePrescription"
                @cancel="closeForm"
              ></prescription-form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      prescriptions: [],
      visits: [],
      doctors: [],
      showAddForm: false,
      editingPrescription: null
    };
  },
  created() {
    this.loadPrescriptions();
    this.loadVisits();
    this.loadDoctors();
  },
  methods: {
    loadPrescriptions() {
      ApiService.get('/api/prescriptions')
        .then(response => {
          this.prescriptions = response.data;
        })
        .catch(error => {
          console.error('获取处方列表失败:', error);
        });
    },
    loadVisits() {
      ApiService.get('/api/visits')
        .then(response => {
          this.visits = response.data;
        })
        .catch(error => {
          console.error('获取就诊记录失败:', error);
        });
    },
    loadDoctors() {
      ApiService.get('/api/doctors')
        .then(response => {
          this.doctors = response.data;
        })
        .catch(error => {
          console.error('获取医生列表失败:', error);
        });
    },
    editPrescription(prescription) {
      this.editingPrescription = { ...prescription };
      this.showAddForm = true;
    },
    deletePrescription(id) {
      if (confirm('确定要删除这个处方吗？')) {
        ApiService.delete(`/api/prescriptions/${id}`)
          .then(() => {
            this.loadPrescriptions();
          })
          .catch(error => {
            console.error('删除处方失败:', error);
          });
      }
    },
    savePrescription(prescriptionData) {
      let request;
      if (prescriptionData.id) {
        // 更新现有处方
        request = ApiService.put(`/api/prescriptions/${prescriptionData.id}`, prescriptionData);
      } else {
        // 创建新处方
        request = ApiService.post('/api/prescriptions', prescriptionData);
      }

      request.then(() => {
        this.closeForm();
        this.loadPrescriptions();
      })
      .catch(error => {
        console.error('保存处方失败:', error);
      });
    },
    closeForm() {
      this.showAddForm = false;
      this.editingPrescription = null;
    }
  },
  components: {
    'prescription-form': {
      props: ['prescription', 'visits', 'doctors'],
      template: `
        <form @submit.prevent="submitForm">
          <div class="form-row">
            <div class="form-group col-md-6">
              <label for="visitId">就诊记录 *</label>
              <select class="form-control" id="visitId" v-model="formData.visitId" required>
                <option value="">请选择就诊记录</option>
                <option v-for="visit in visits" :value="visit.id" :key="visit.id">
                  {{ visit.patient ? visit.patient.name : 'N/A' }} - {{ visit.visitDate | formatDate }}
                </option>
              </select>
            </div>
            <div class="form-group col-md-6">
              <label for="doctorId">医生 *</label>
              <select class="form-control" id="doctorId" v-model="formData.doctorId" required>
                <option value="">请选择医生</option>
                <option v-for="doctor in doctors" :value="doctor.id" :key="doctor.id">
                  {{ doctor.name }} ({{ doctor.title }})
                </option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <label for="prescriptionName">处方名称</label>
            <input type="text" class="form-control" id="prescriptionName" v-model="formData.prescriptionName">
          </div>
          <div class="form-group">
            <label for="decoctionMethod">煎药方法</label>
            <textarea class="form-control" id="decoctionMethod" v-model="formData.decoctionMethod" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label for="treatmentDuration">治疗天数</label>
            <input type="number" class="form-control" id="treatmentDuration" v-model="formData.treatmentDuration">
          </div>
          <div class="form-group">
            <label for="doctorAdvice">医嘱</label>
            <textarea class="form-control" id="doctorAdvice" v-model="formData.doctorAdvice" rows="3"></textarea>
          </div>
          <div class="form-group">
            <label for="status">状态</label>
            <select class="form-control" id="status" v-model="formData.status">
              <option value="0">未取药</option>
              <option value="1">已取药</option>
              <option value="2">已完成</option>
            </select>
          </div>
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-secondary" @click="$emit('cancel')">取消</button>
            <button type="submit" class="btn btn-primary">保存</button>
          </div>
        </form>
      `,
      data() {
        return {
          formData: {
            visitId: '',
            doctorId: '',
            prescriptionName: '',
            decoctionMethod: '',
            treatmentDuration: null,
            doctorAdvice: '',
            status: 0
          }
        };
      },
      filters: {
        formatDate(dateString) {
          if (!dateString) return '';
          const date = new Date(dateString);
          return date.toLocaleString('zh-CN');
        }
      },
      created() {
        if (this.prescription) {
          this.formData = {
            ...this.prescription,
            visitId: this.prescription.visit ? this.prescription.visit.id : '',
            doctorId: this.prescription.doctor ? this.prescription.doctor.id : ''
          };
        }
      },
      watch: {
        prescription(newVal) {
          if (newVal) {
            this.formData = {
              ...newVal,
              visitId: newVal.visit ? newVal.visit.id : '',
              doctorId: newVal.doctor ? newVal.doctor.id : ''
            };
          } else {
            this.resetForm();
          }
        }
      },
      methods: {
        submitForm() {
          // 转换表单数据以匹配API要求
          const prescriptionData = {
            ...this.formData,
            visit: { id: this.formData.visitId },
            doctor: { id: this.formData.doctorId }
          };
          this.$emit('save', prescriptionData);
        },
        resetForm() {
          this.formData = {
            visitId: '',
            doctorId: '',
            prescriptionName: '',
            decoctionMethod: '',
            treatmentDuration: null,
            doctorAdvice: '',
            status: 0
          };
        }
      }
    }
  }
};

// 诊断图片列表组件
const DiagnosticImageList = {
  template: `
    <div>
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="mb-0">诊断图片列表</h5>
          <button class="btn btn-primary" @click="showAddForm = true">上传诊断图片</button>
        </div>
        <div class="card-body">
          <div class="form-row mb-3">
            <div class="form-group col-md-4">
              <label for="filterVisitId">按就诊ID筛选</label>
              <input type="number" class="form-control" id="filterVisitId" v-model="filterVisitId" placeholder="输入就诊ID">
            </div>
            <div class="form-group col-md-4">
              <label for="filterImageType">按图片类型筛选</label>
              <select class="form-control" id="filterImageType" v-model="filterImageType">
                <option value="">全部类型</option>
                <option value="tongue">舌象</option>
                <option value="face">面象</option>
                <option value="pulse">脉象</option>
              </select>
            </div>
            <div class="form-group col-md-4 d-flex align-items-end">
              <button class="btn btn-secondary mr-2" @click="loadDiagnosticImages">筛选</button>
              <button class="btn btn-outline-secondary" @click="resetFilters">重置</button>
            </div>
          </div>

          <div class="row">
            <div v-for="image in diagnosticImages" :key="image.id" class="col-md-3 mb-3">
              <div class="card">
                <div class="card-body text-center">
                  <img :src="image.imagePath ? \`\${window.API_BASE_URL}/\${image.imagePath}\` : 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZWVlIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPklNR0ZJTEU8L3RleHQ+PC9zdmc+'"
                       :alt="image.imageName"
                       :class="['diagnostic-image', image.imageType + '-image']"
                       @click="showImageDetails(image)"
                       style="cursor: pointer;">
                  <h6 class="card-title mt-2">{{ image.imageName }}</h6>
                  <p class="card-text">
                    <small class="text-muted">类型: {{ imageTypeText(image.imageType) }}</small><br>
                    <small class="text-muted">尺寸: {{ image.width }}x{{ image.height }}</small><br>
                    <small class="text-muted">大小: {{ formatFileSize(image.imageSize) }}</small>
                  </p>
                </div>
                <div class="card-footer bg-transparent text-center">
                  <button class="btn btn-sm btn-danger" @click="deleteDiagnosticImage(image.id)">删除</button>
                </div>
              </div>
            </div>
          </div>

          <div v-if="diagnosticImages.length === 0" class="text-center text-muted py-5">
            <p>暂无诊断图片</p>
          </div>
        </div>
      </div>

      <!-- 诊断图片表单模态框 -->
      <div class="modal fade" :class="{ show: showAddForm }" :style="{ display: showAddForm ? 'block' : 'none' }" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">上传诊断图片</h5>
              <button type="button" class="close" @click="closeForm">
                <span>&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <diagnostic-image-form @save="saveDiagnosticImage" @cancel="closeForm"></diagnostic-image-form>
            </div>
          </div>
        </div>
      </div>

      <!-- 图片详情模态框 -->
      <div class="modal fade" :class="{ show: showImageDetailsModal }" :style="{ display: showImageDetailsModal ? 'block' : 'none' }" tabindex="-1">
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ selectedImage.imageName }}</h5>
              <button type="button" class="close" @click="showImageDetailsModal = false">
                <span>&times;</span>
              </button>
            </div>
            <div class="modal-body text-center">
              <img :src="selectedImage.imagePath ? \`\${window.API_BASE_URL}/\${selectedImage.imagePath}\` : 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZWVlIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPklNR0ZJTEU8L3RleHQ+PC9zdmc+'"
                   :alt="selectedImage.imageName"
                   :class="['img-fluid', selectedImage.imageType + '-image']"
                   style="max-height: 400px;">
              <div class="mt-3">
                <p><strong>类型:</strong> {{ imageTypeText(selectedImage.imageType) }}</p>
                <p><strong>尺寸:</strong> {{ selectedImage.width }}x{{ selectedImage.height }}</p>
                <p><strong>大小:</strong> {{ formatFileSize(selectedImage.imageSize) }}</p>
                <p><strong>格式:</strong> {{ selectedImage.processedFormat }}</p>
                <p><strong>描述:</strong> {{ selectedImage.description || '无' }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      diagnosticImages: [],
      filterVisitId: '',
      filterImageType: '',
      showAddForm: false,
      showImageDetailsModal: false,
      selectedImage: {}
    };
  },
  created() {
    this.loadDiagnosticImages();
  },
  methods: {
    loadDiagnosticImages() {
      let endpoint = '/api/diagnostic-images';
      const params = [];

      if (this.filterVisitId) {
        endpoint = `/api/diagnostic-images/visit/${this.filterVisitId}`;
      } else if (this.filterImageType) {
        endpoint = `/api/diagnostic-images/type/${this.filterImageType}`;
      }

      ApiService.get(endpoint)
        .then(response => {
          this.diagnosticImages = response.data;
        })
        .catch(error => {
          console.error('获取诊断图片失败:', error);
        });
    },
    imageTypeText(type) {
      const types = {
        'tongue': '舌象',
        'face': '面象',
        'pulse': '脉象'
      };
      return types[type] || type;
    },
    formatFileSize(size) {
      if (!size) return '0 B';
      const units = ['B', 'KB', 'MB', 'GB'];
      let i = 0;
      while (size >= 1024 && i < units.length - 1) {
        size /= 1024;
        i++;
      }
      return Math.round(size * 100) / 100 + ' ' + units[i];
    },
    deleteDiagnosticImage(id) {
      if (confirm('确定要删除这张诊断图片吗？')) {
        ApiService.delete(`/api/diagnostic-images/${id}`)
          .then(() => {
            this.loadDiagnosticImages();
          })
          .catch(error => {
            console.error('删除诊断图片失败:', error);
          });
      }
    },
    saveDiagnosticImage(formData) {
      const data = new FormData();
      data.append('visitId', formData.visitId);
      data.append('file', formData.file);
      data.append('imageType', formData.imageType);
      data.append('description', formData.description);

      ApiService.post('/api/diagnostic-images/upload', data, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(() => {
        this.closeForm();
        this.loadDiagnosticImages();
      })
      .catch(error => {
        console.error('上传诊断图片失败:', error);
      });
    },
    closeForm() {
      this.showAddForm = false;
    },
    resetFilters() {
      this.filterVisitId = '';
      this.filterImageType = '';
      this.loadDiagnosticImages();
    },
    showImageDetails(image) {
      this.selectedImage = image;
      this.showImageDetailsModal = true;
    }
  },
  components: {
    'diagnostic-image-form': {
      template: `
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label for="visitId">就诊记录ID *</label>
            <input type="number" class="form-control" id="visitId" v-model="formData.visitId" required>
            <small class="form-text text-muted">请输入关联的就诊记录ID</small>
          </div>
          <div class="form-group">
            <label for="imageType">图片类型 *</label>
            <select class="form-control" id="imageType" v-model="formData.imageType" required>
              <option value="tongue">舌象</option>
              <option value="face">面象</option>
              <option value="pulse">脉象</option>
            </select>
          </div>
          <div class="form-group">
            <label for="file">选择图片文件 *</label>
            <input type="file" class="form-control" id="file" @change="onFileChange" accept="image/*" required>
          </div>
          <div class="form-group">
            <label for="description">图片描述</label>
            <textarea class="form-control" id="description" v-model="formData.description" rows="2"></textarea>
          </div>
          <div class="d-flex justify-content-end">
            <button type="button" class="btn btn-secondary" @click="$emit('cancel')">取消</button>
            <button type="submit" class="btn btn-primary">上传</button>
          </div>
        </form>
      `,
      data() {
        return {
          formData: {
            visitId: '',
            imageType: 'tongue',
            file: null,
            description: ''
          }
        };
      },
      methods: {
        onFileChange(e) {
          this.formData.file = e.target.files[0];
        },
        submitForm() {
          this.$emit('save', { ...this.formData });
        }
      }
    }
  }
};

// 患者自诊模式组件
const SelfDiagnosis = {
  template: `
    <div class="self-diagnosis">
      <div class="card">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="mb-0">患者自诊模式</h5>
        </div>
        <div class="card-body">
          <div class="chat-container">
            <div class="chat-messages" ref="chatMessages">
              <div
                v-for="(msg, index) in messages"
                :key="index + '_' + msg.sender"
                :class="['message', msg.sender]"
              >
                <div class="message-content">
                  <strong v-if="msg.sender === 'system'">系统：</strong>
                  <strong v-if="msg.sender === 'user'">我：</strong>
                  <span>{{ msg.content }}</span>
                </div>
              </div>
            </div>

            <div class="chat-input-area mt-3">
              <div v-if="currentStep === 'image_upload'" class="image-upload-section">
                <p>请上传您的舌象图片：</p>
                <input type="file" @change="onImageChange" accept="image/*" id="imageUpload" class="form-control">
                <button
                  @click="submitImage"
                  :disabled="!selectedImage"
                  class="btn btn-primary mt-2"
                >
                  提交图片
                </button>
              </div>

              <div v-else-if="currentStep !== 'completed'" class="input-group">
                <input
                  type="text"
                  v-model="userInput"
                  @keyup.enter="sendResponse"
                  :placeholder="inputPlaceholder"
                  class="form-control"
                  :disabled="isProcessing"
                >
                <div class="input-group-append">
                  <button
                    @click="sendResponse"
                    class="btn btn-primary"
                    type="button"
                    :disabled="!userInput.trim() || isProcessing"
                  >
                    发送
                  </button>
                </div>
              </div>

              <div v-else class="text-center">
                <p>问诊已完成，正在为您分析诊断结果...</p>
                <button @click="startNewDiagnosis" class="btn btn-success">开始新的自诊</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      messages: [],
      userInput: '',
      currentStep: 'init', // init, symptoms_general, symptoms_respiratory, symptoms_digestive, symptoms_other, image_upload, additional, completed
      diagnosisData: {
        patient: {
          name: '',
          gender: null,
          age: null,
          idCard: '',
          phone: ''
        },
        visit: {
          chiefComplaint: '',
          symptoms: '',
          initialVisitClinicalManifestation: '',
          tongueDiagnosis: '',
          pulseDiagnosis: '',
          tcmDiagnosis: '',
          westernDiagnosis: '',
          patternDifferentiation: '',
          treatmentPlan: '',
          visitType: 0 // 初诊
        },
        diagnosticImages: []
      },
      selectedImage: null,
      isProcessing: false,
      conversationFlow: {
        init: [
          "您好，我是中医智能问诊助手，现在开始为您进行自诊。请先告诉我您的姓名。",
          "请问您的性别？（男/女/其他）",
          "请问您多大年纪？",
          "请问您目前最不舒服的症状是什么？"
        ],
        symptoms_general: [
          "您有没有发烧？",
          "怕冷吗？",
          "出汗吗？",
          "手脚凉不凉？"
        ],
        symptoms_respiratory: [
          "有没有咳嗽？",
          "有痰吗？",
          "痰是什么颜色的？（偏黄还是偏白）",
          "嗓子疼不疼？"
        ],
        symptoms_digestive: [
          "胃口怎么样？",
          "大便正常吗？"
        ],
        symptoms_other: [
          "口渴吗？想喝热水还是凉水？",
          "浑身疼不疼？"
        ],
        additional: [
          "您还有其他症状吗？"
        ]
      },
      currentQuestionIndex: 0
    };
  },
  computed: {
    inputPlaceholder() {
      if (this.currentStep === 'init') {
        switch(this.currentQuestionIndex) {
          case 0: return "请输入您的姓名";
          case 1: return "请输入性别（男/女/其他）";
          case 2: return "请输入年龄";
          case 3: return "请输入主诉症状";
          default: return "请输入...";
        }
      }
      return "请输入回答...";
    }
  },
  mounted() {
    this.startDiagnosis();
  },
  updated() {
    // 自动滚动到底部
    this.$nextTick(() => {
      if (this.$refs && this.$refs.chatMessages) {
        this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight;
      }
    });
  },
  methods: {
    startDiagnosis() {
      this.messages = [];
      this.currentStep = 'init';
      this.currentQuestionIndex = 0;
      this.addSystemMessage(this.conversationFlow.init[0]);
    },

    startNewDiagnosis() {
      this.messages = [];
      this.currentStep = 'init';
      this.currentQuestionIndex = 0;
      this.diagnosisData = {
        patient: {
          name: '',
          gender: null,
          age: null,
          idCard: '',
          phone: ''
        },
        visit: {
          chiefComplaint: '',
          symptoms: '',
          initialVisitClinicalManifestation: '',
          tongueDiagnosis: '',
          pulseDiagnosis: '',
          tcmDiagnosis: '',
          westernDiagnosis: '',
          patternDifferentiation: '',
          treatmentPlan: '',
          visitType: 0
        },
        diagnosticImages: []
      };
      this.selectedImage = null;
      this.addSystemMessage(this.conversationFlow.init[0]);
    },

    addSystemMessage(content) {
      this.messages.push({
        sender: 'system',
        content: content
      });
    },

    addUserMessage(content) {
      this.messages.push({
        sender: 'user',
        content: content
      });
    },


    sendResponse() {
      if (!this.userInput.trim() || this.isProcessing) return;

      const userResponse = this.userInput.trim();
      this.addUserMessage(userResponse);

      this.processUserResponse(userResponse);
      this.userInput = '';
    },

    processUserResponse(response) {
      this.isProcessing = true;

      // 根据当前步骤处理用户响应
      if (this.currentStep === 'init') {
        this.handleInitStep(response);
      } else if (this.currentStep === 'symptoms_general') {
        this.handleSymptomsGeneralStep(response);
      } else if (this.currentStep === 'symptoms_respiratory') {
        this.handleSymptomsRespiratoryStep(response);
      } else if (this.currentStep === 'symptoms_digestive') {
        this.handleSymptomsDigestiveStep(response);
      } else if (this.currentStep === 'symptoms_other') {
        this.handleSymptomsOtherStep(response);
      } else if (this.currentStep === 'additional') {
        this.handleAdditionalSymptomsStep(response);
      }

      setTimeout(() => {
        this.isProcessing = false;
      }, 500);
    },

    handleInitStep(response) {
      // 处理基本信息收集
      switch(this.currentQuestionIndex) {
        case 0: // 姓名
          this.diagnosisData.patient.name = response;
          this.currentQuestionIndex++;
          this.addSystemMessage(this.conversationFlow.init[1]);
          break;
        case 1: // 性别
          if (response.includes('男') || response.toLowerCase() === 'male') {
            this.diagnosisData.patient.gender = 1;
          } else if (response.includes('女') || response.toLowerCase() === 'female') {
            this.diagnosisData.patient.gender = 0;
          } else {
            this.diagnosisData.patient.gender = 2;
          }
          this.currentQuestionIndex++;
          this.addSystemMessage(this.conversationFlow.init[2]);
          break;
        case 2: // 年龄
          const age = parseInt(response);
          if (!isNaN(age) && age > 0) {
            this.diagnosisData.patient.age = age;
          }
          this.currentQuestionIndex++;
          this.addSystemMessage(this.conversationFlow.init[3]);
          break;
        case 3: // 主诉症状
          this.diagnosisData.visit.chiefComplaint = response;
          this.currentQuestionIndex = 0;
          this.currentStep = 'symptoms_general';
          this.addSystemMessage(this.conversationFlow.symptoms_general[0]);
          break;
      }
    },

    handleSymptomsGeneralStep(response) {
      // 添加症状到临床表现
      if (this.diagnosisData.visit.initialVisitClinicalManifestation) {
        this.diagnosisData.visit.initialVisitClinicalManifestation += "；";
      }
      this.diagnosisData.visit.initialVisitClinicalManifestation +=
        `${this.conversationFlow.symptoms_general[this.currentQuestionIndex]} ${response}`;

      this.currentQuestionIndex++;

      if (this.currentQuestionIndex < this.conversationFlow.symptoms_general.length) {
        this.addSystemMessage(this.conversationFlow.symptoms_general[this.currentQuestionIndex]);
      } else {
        this.currentQuestionIndex = 0;
        this.currentStep = 'symptoms_respiratory';
        this.addSystemMessage(this.conversationFlow.symptoms_respiratory[0]);
      }
    },

    handleSymptomsRespiratoryStep(response) {
      // 添加症状到临床表现
      if (this.diagnosisData.visit.initialVisitClinicalManifestation) {
        this.diagnosisData.visit.initialVisitClinicalManifestation += "；";
      }
      this.diagnosisData.visit.initialVisitClinicalManifestation +=
        `${this.conversationFlow.symptoms_respiratory[this.currentQuestionIndex]} ${response}`;

      this.currentQuestionIndex++;

      if (this.currentQuestionIndex < this.conversationFlow.symptoms_respiratory.length) {
        this.addSystemMessage(this.conversationFlow.symptoms_respiratory[this.currentQuestionIndex]);
      } else {
        this.currentQuestionIndex = 0;
        this.currentStep = 'symptoms_digestive';
        this.addSystemMessage(this.conversationFlow.symptoms_digestive[0]);
      }
    },

    handleSymptomsDigestiveStep(response) {
      // 添加症状到临床表现
      if (this.diagnosisData.visit.initialVisitClinicalManifestation) {
        this.diagnosisData.visit.initialVisitClinicalManifestation += "；";
      }
      this.diagnosisData.visit.initialVisitClinicalManifestation +=
        `${this.conversationFlow.symptoms_digestive[this.currentQuestionIndex]} ${response}`;

      this.currentQuestionIndex++;

      if (this.currentQuestionIndex < this.conversationFlow.symptoms_digestive.length) {
        this.addSystemMessage(this.conversationFlow.symptoms_digestive[this.currentQuestionIndex]);
      } else {
        this.currentQuestionIndex = 0;
        this.currentStep = 'symptoms_other';
        this.addSystemMessage(this.conversationFlow.symptoms_other[0]);
      }
    },

    handleSymptomsOtherStep(response) {
      // 添加症状到临床表现
      if (this.diagnosisData.visit.initialVisitClinicalManifestation) {
        this.diagnosisData.visit.initialVisitClinicalManifestation += "；";
      }
      this.diagnosisData.visit.initialVisitClinicalManifestation +=
        `${this.conversationFlow.symptoms_other[this.currentQuestionIndex]} ${response}`;

      this.currentQuestionIndex++;

      if (this.currentQuestionIndex < this.conversationFlow.symptoms_other.length) {
        this.addSystemMessage(this.conversationFlow.symptoms_other[this.currentQuestionIndex]);
      } else {
        this.currentQuestionIndex = 0;
        this.currentStep = 'image_upload';
        this.addSystemMessage("请您伸出舌头，拍一张清晰的照片发过来。");
      }
    },

    handleAdditionalSymptomsStep(response) {
      // 添加其他症状
      if (this.diagnosisData.visit.initialVisitClinicalManifestation) {
        this.diagnosisData.visit.initialVisitClinicalManifestation += "；其他症状：" + response;
      } else {
        this.diagnosisData.visit.initialVisitClinicalManifestation = "其他症状：" + response;
      }

      this.currentStep = 'completed';
      this.addSystemMessage("问诊已完成，正在为您分析诊断结果...");

      // 保存数据到后端
      this.saveDiagnosisData();
    },

    onImageChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.selectedImage = file;
      }
    },

    submitImage() {
      if (!this.selectedImage) return;

      // 上传舌象图片并触发AI分析
      // 设置ID卡号为空字符串，让后端自动生成自诊ID
      this.diagnosisData.patient.idCard = "";

      // 创建患者和就诊记录
      ApiService.post('/api/patients', this.diagnosisData.patient)
        .then(patientResponse => {
          const patient = patientResponse.data;
          console.log("患者创建成功:", patient);

          // 创建就诊记录
          const visitData = {
            ...this.diagnosisData.visit,
            patient: { id: patient.id },
            visitDate: new Date().toISOString(),
            medicalRecordNumber: `SD-${Date.now()}`
          };

          return ApiService.post('/api/visits', visitData);
        })
        .then(visitResponse => {
          const visit = visitResponse.data;
          console.log("就诊记录创建成功:", visit);

          // 上传舌象图片并触发AI分析
          const formData = new FormData();
          formData.append('file', this.selectedImage);
          formData.append('imageType', 'tongue');
          formData.append('description', '自诊舌象图片');

          console.log("准备上传舌象图片并触发AI分析，visitId:", visit.id);
          // 调用自诊专用的图片上传及AI分析接口
          return ApiService.post(`/api/self-diagnosis/upload-tongue-image/${visit.id}`, formData);
        })
        .then(uploadResponse => {
          console.log("图片上传并AI分析成功:", uploadResponse);
          this.addSystemMessage("图片上传成功并已进行AI分析！");

          // 显示AI分析结果 - 直接显示VLLM返回的处方组成内容
          if (uploadResponse.data && uploadResponse.data.aiAnalysisResult) {
            const aiResult = uploadResponse.data.aiAnalysisResult;

            // 如果有完整的AI分析结果，显示原始VLLM响应内容
            if (aiResult['最终结果'] && aiResult['最终结果']['处方组成']) {
              let resultMessage = "AI诊断结果：\n";
              resultMessage += aiResult['最终结果']['处方组成'];
              this.addSystemMessage(resultMessage);
            } else if (aiResult['处方组成']) {
              // 如果直接有处方组成字段
              let resultMessage = "AI诊断结果：\n";
              resultMessage += aiResult['处方组成'];
              this.addSystemMessage(resultMessage);
            } else {
              // 如果没有处方组成，则显示整体AI分析结果
              this.addSystemMessage("AI诊断结果：\n" + JSON.stringify(aiResult, null, 2));
            }
          }

          this.currentQuestionIndex = 0;
          this.currentStep = 'additional';
          this.addSystemMessage(this.conversationFlow.additional[0]);
        })
        .catch(error => {
          console.error('上传图片或AI分析失败:', error);
          console.error('错误详情:', error.response || error.message || error);
          if (error.response) {
            console.error('错误状态:', error.response.status);
            this.addSystemMessage(`图片上传或AI分析失败: ${error.response.data?.message || error.message}`);
          } else {
            this.addSystemMessage("图片上传或AI分析失败，请稍后再试。");
          }
        });
    },

    saveDiagnosisData() {
      // 为自诊患者设置ID卡号为空字符串，让后端自动生成自诊ID
      this.diagnosisData.patient.idCard = "";

      // 直接创建新患者（自诊模式下始终创建新患者）
      ApiService.post('/api/patients', this.diagnosisData.patient)
        .then(patientResponse => {
          const patient = patientResponse.data;
          console.log("自诊患者创建成功:", patient);

          // 创建就诊记录
          const visitData = {
            ...this.diagnosisData.visit,
            patient: { id: patient.id },
            visitDate: new Date().toISOString()
          };

          return ApiService.post('/api/visits', visitData)
            .then(visitResponse => {
              const visit = visitResponse.data;

              // 在保存后触发最终的AI分析，获取完整的诊断结果
              return ApiService.post(`/api/self-diagnosis/complete-self-diagnosis/${visit.id}`)
                .then(aiResponse => {
                  this.addSystemMessage("您的自诊信息已保存成功！");

                  // 显示AI分析的最终结果 - 直接显示VLLM返回的处方组成内容
                  if (aiResponse.data && aiResponse.data.result) {
                    const aiResult = aiResponse.data.result;

                    // 如果有完整的AI分析结果，显示原始VLLM响应内容
                    if (aiResult['最终结果'] && aiResult['最终结果']['处方组成']) {
                      let resultMessage = "\n最终AI诊断结果：\n";
                      resultMessage += aiResult['最终结果']['处方组成'];
                      this.addSystemMessage(resultMessage);
                    } else if (aiResult['处方组成']) {
                      // 如果直接有处方组成字段
                      let resultMessage = "\n最终AI诊断结果：\n";
                      resultMessage += aiResult['处方组成'];
                      this.addSystemMessage(resultMessage);
                    } else {
                      // 如果没有处方组成，则显示整体AI分析结果
                      this.addSystemMessage("\n最终AI诊断结果：\n" + JSON.stringify(aiResult, null, 2));
                    }
                  }

                  console.log('诊断数据保存成功', { patient, visit, aiResult: aiResponse.data });
                })
                .catch(aiError => {
                  console.error('AI分析失败:', aiError);
                  this.addSystemMessage("数据已保存，但AI分析出现错误。");
                  console.log('诊断数据保存成功但AI分析失败', { patient, visit });
                });
            });
        })
        .catch(error => {
          console.error('保存诊断数据失败:', error);
          this.addSystemMessage("保存数据时出现错误，请稍后再试。");
        });
    }
  }
};

// 仪表板组件
const Dashboard = {
  template: `
    <div>
      <div class="header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">仪表板</h5>
      </div>

      <div class="row">
        <div class="col-md-3 mb-3">
          <div class="card bg-primary text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.patientCount }}</h4>
                  <p class="mb-0">患者总数</p>
                </div>
                <i class="fa fa-users fa-2x"></i>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card bg-info text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.doctorCount }}</h4>
                  <p class="mb-0">医生总数</p>
                </div>
                <i class="fa fa-user-md fa-2x"></i>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card bg-success text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.visitCount }}</h4>
                  <p class="mb-0">就诊总数</p>
                </div>
                <i class="fa fa-stethoscope fa-2x"></i>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3 mb-3">
          <div class="card bg-warning text-white">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h4>{{ stats.prescriptionCount }}</h4>
                  <p class="mb-0">处方总数</p>
                </div>
                <i class="fa fa-file-text-o fa-2x"></i>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">最近就诊记录</h5>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-sm">
                  <thead>
                    <tr>
                      <th>患者</th>
                      <th>医生</th>
                      <th>类型</th>
                      <th>日期</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="visit in recentVisits" :key="visit.id">
                      <td>{{ visit.patient ? visit.patient.name : 'N/A' }}</td>
                      <td>{{ visit.doctor ? visit.doctor.name : 'N/A' }}</td>
                      <td>{{ visit.visitType === 0 ? '初诊' : '复诊' }}</td>
                      <td>{{ visit.visitDate | formatDate }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">最近上传的诊断图片</h5>
            </div>
            <div class="card-body">
              <div class="table-responsive">
                <table class="table table-sm">
                  <thead>
                    <tr>
                      <th>图片名</th>
                      <th>类型</th>
                      <th>就诊ID</th>
                      <th>上传时间</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="image in recentImages" :key="image.id">
                      <td>{{ image.imageName | truncate(15) }}</td>
                      <td>{{ imageTypeText(image.imageType) }}</td>
                      <td>{{ image.visit ? image.visit.id : 'N/A' }}</td>
                      <td>{{ image.createTime | formatDate }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  data() {
    return {
      stats: {
        patientCount: 0,
        doctorCount: 0,
        visitCount: 0,
        prescriptionCount: 0
      },
      recentVisits: [],
      recentImages: []
    };
  },
  created() {
    this.loadStats();
    this.loadRecentData();
  },
  filters: {
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleString('zh-CN');
    },
    truncate(value, limit) {
      if (!value) return '';
      value = value.toString();
      if (value.length <= limit) return value;
      return value.substring(0, limit) + '...';
    }
  },
  methods: {
    imageTypeText(type) {
      const types = {
        'tongue': '舌象',
        'face': '面象',
        'pulse': '脉象'
      };
      return types[type] || type;
    },
    loadStats() {
      // 获取仪表板统计数据
      Promise.all([
        ApiService.get('/api/patients'),
        ApiService.get('/api/doctors'),
        ApiService.get('/api/visits'),
        ApiService.get('/api/prescriptions')
      ])
      .then(responses => {
        this.stats.patientCount = responses[0].data.length;
        this.stats.doctorCount = responses[1].data.length;
        this.stats.visitCount = responses[2].data.length;
        this.stats.prescriptionCount = responses[3].data.length;
      })
      .catch(error => {
        console.error('获取统计数据失败:', error);
      });
    },
    loadRecentData() {
      // 获取最近就诊记录
      ApiService.get('/api/visits')
        .then(response => {
          // 按日期排序并取最近5条
          this.recentVisits = response.data
            .sort((a, b) => new Date(b.visitDate) - new Date(a.visitDate))
            .slice(0, 5);
        })
        .catch(error => {
          console.error('获取最近就诊记录失败:', error);
        });

      // 获取最近诊断图片
      ApiService.get('/api/diagnostic-images')
        .then(response => {
          // 按日期排序并取最近5条
          this.recentImages = response.data
            .sort((a, b) => new Date(b.createTime || b.id) - new Date(a.createTime || a.id))
            .slice(0, 5);
        })
        .catch(error => {
          console.error('获取最近诊断图片失败:', error);
        });
    }
  }
};

// 定义路由
const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/dashboard', component: Dashboard },
  { path: '/patients', component: PatientList },
  { path: '/doctors', component: DoctorList },
  { path: '/visits', component: VisitList },
  { path: '/prescriptions', component: PrescriptionList },
  { path: '/diagnostic-images', component: DiagnosticImageList },
  { path: '/self-diagnosis', component: SelfDiagnosis }
];

// 创建路由实例
const router = new VueRouter({
  mode: 'hash',
  routes
});

// 创建Vue根实例
const app = new Vue({
  router,
  el: '#app', // 指定挂载点，而不是用模板替换内容
  data() {
    return {
      currentTime: new Date().toLocaleString('zh-CN')
    };
  },
  mounted() {
    // 更新当前时间
    this.updateTime();
    setInterval(() => {
      this.updateTime();
    }, 1000);

    // 更新时间显示
    const timeElement = document.getElementById('current-time');
    if (timeElement) {
      timeElement.textContent = this.currentTime;
    }
  },
  methods: {
    navigateTo(route) {
      this.$router.push(route);
    },
    updateTime() {
      this.currentTime = new Date().toLocaleString('zh-CN');
      const timeElement = document.getElementById('current-time');
      if (timeElement) {
        timeElement.textContent = this.currentTime;
      }
    }
  }
});

// 将Vue实例和导航方法挂载到window对象，供HTML中的onclick事件调用
window.vueApp = app;

// 改进的navigateTo函数，支持在Vue应用初始化之前调用
window.navigateTo = function(route) {
  if (window.vueApp && window.vueApp.$router) {
    window.vueApp.$router.push(route);
  } else {
    console.error('Vue应用未初始化或路由未就绪');
    // 添加一个延迟重试机制
    setTimeout(() => {
      if (window.vueApp && window.vueApp.$router) {
        window.vueApp.$router.push(route);
      }
    }, 100);
  }
};