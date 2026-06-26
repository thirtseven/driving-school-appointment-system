const API_BASE_URL = window.location.origin;
let appointmentModal, studentModal, statusModal, deleteModal, toast;

document.addEventListener('DOMContentLoaded', function() {
    appointmentModal = new bootstrap.Modal(document.getElementById('appointmentModal'));
    studentModal = new bootstrap.Modal(document.getElementById('studentModal'));
    statusModal = new bootstrap.Modal(document.getElementById('statusModal'));
    deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
    toast = new bootstrap.Toast(document.getElementById('toast'));
    
    loadDashboardData();
    loadAppointments();
    loadStudents();
    loadStudentOptions();
});

function showSection(sectionId, linkElement) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    document.getElementById(sectionId).classList.add('active');
    if (linkElement) {
        linkElement.classList.add('active');
    }
    
    if (sectionId === 'appointments') loadAppointments();
    if (sectionId === 'students') loadStudents();
    if (sectionId === 'dashboard') loadDashboardData();
}

function showToast(title, message, isError = false) {
    document.getElementById('toastTitle').textContent = title;
    document.getElementById('toastMessage').textContent = message;
    document.getElementById('toast').className = 'toast' + (isError ? ' border-danger' : ' border-success');
    toast.show();
}

async function loadDashboardData() {
    try {
        const statsResponse = await fetch(`${API_BASE_URL}/api/statistics`);
        const stats = await statsResponse.json();

        document.getElementById('totalAppointments').textContent = stats.totalAppointments || 0;
        document.getElementById('totalRevenue').textContent = `¥${stats.totalRevenue || 0}`;
        document.getElementById('totalStudents').textContent = stats.totalStudents || 0;
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

async function loadAppointments() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/appointments`);
        const data = await response.json();
        const tableBody = document.getElementById('appointmentsTable');
        tableBody.innerHTML = '';

        if (data.appointments && data.appointments.length > 0) {
            data.appointments.forEach(apt => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><code>${apt.id}</code></td>
                    <td>${apt.studentName}</td>
                    <td>${apt.courseName}</td>
                    <td>${apt.classTime}</td>
                    <td>¥${apt.finalPrice.toFixed(2)}</td>
                    <td><span class="badge bg-${getStatusColor(apt.status)}">${getStatusText(apt.status)}</span></td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="showStatusModal('${apt.id}', '${apt.status}')">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="showDeleteModal('${apt.id}', 'appointment')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">暂无预约记录，点击"新增预约"创建</td></tr>';
        }
    } catch (error) {
        console.error('Error loading appointments:', error);
    }
}

async function loadStudents() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/students`);
        const data = await response.json();
        const tableBody = document.getElementById('studentsTable');
        tableBody.innerHTML = '';

        if (data.students && data.students.length > 0) {
            data.students.forEach(student => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><code>${student.id}</code></td>
                    <td>${student.name}</td>
                    <td>${student.phone}</td>
                    <td>${getStudentTypeText(student.type)}</td>
                    <td>${student.discount}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-primary me-1" onclick="editStudent('${student.id}', '${student.name}', '${student.phone}', '${student.type}')">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" onclick="showDeleteModal('${student.id}', 'student')">
                            <i class="fas fa-trash"></i>
                        </button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">暂无学员，点击"新增学员"创建</td></tr>';
        }
    } catch (error) {
        console.error('Error loading students:', error);
    }
}

async function loadStudentOptions() {
    try {
        const response = await fetch(`${API_BASE_URL}/api/students`);
        const data = await response.json();
        const select = document.getElementById('appointmentStudent');
        select.innerHTML = '<option value="">请选择学员</option>';
        
        if (data.students) {
            data.students.forEach(student => {
                const option = document.createElement('option');
                option.value = student.id;
                option.textContent = `${student.name} (${getStudentTypeText(student.type)})`;
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading student options:', error);
    }
}

function showAppointmentModal() {
    document.getElementById('appointmentModalTitle').textContent = '新增预约';
    document.getElementById('appointmentForm').reset();
    document.getElementById('appointmentId').value = '';
    loadStudentOptions();
    appointmentModal.show();
}

async function saveAppointment() {
    const studentId = document.getElementById('appointmentStudent').value;
    const courseType = document.getElementById('appointmentCourse').value;
    const classTime = document.getElementById('appointmentTime').value;
    const withSimulation = document.getElementById('withSimulation').checked;
    const withCoaching = document.getElementById('withCoaching').checked;

    if (!studentId || !classTime) {
        showToast('错误', '请填写所有必填字段', true);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/appointments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ studentId, courseType, classTime, withSimulation, withCoaching })
        });

        if (response.ok) {
            appointmentModal.hide();
            showToast('成功', '预约创建成功');
            loadAppointments();
            loadDashboardData();
        } else {
            const error = await response.json();
            showToast('错误', error.message || '创建失败', true);
        }
    } catch (error) {
        showToast('错误', '网络错误: ' + error.message, true);
    }
}

function showStatusModal(id, currentStatus) {
    document.getElementById('statusAppointmentId').value = id;
    document.getElementById('newStatus').value = currentStatus;
    statusModal.show();
}

async function updateAppointmentStatus() {
    const id = document.getElementById('statusAppointmentId').value;
    const status = document.getElementById('newStatus').value;

    try {
        const response = await fetch(`${API_BASE_URL}/api/appointments/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status })
        });

        if (response.ok) {
            statusModal.hide();
            showToast('成功', '状态更新成功');
            loadAppointments();
        } else {
            showToast('错误', '更新失败', true);
        }
    } catch (error) {
        showToast('错误', '网络错误: ' + error.message, true);
    }
}

function showStudentModal() {
    document.getElementById('studentModalTitle').textContent = '新增学员';
    document.getElementById('studentForm').reset();
    document.getElementById('studentId').value = '';
    studentModal.show();
}

function editStudent(id, name, phone, type) {
    document.getElementById('studentModalTitle').textContent = '编辑学员';
    document.getElementById('studentId').value = id;
    document.getElementById('studentName').value = name;
    document.getElementById('studentPhone').value = phone;
    document.getElementById('studentType').value = type;
    studentModal.show();
}

async function saveStudent() {
    const id = document.getElementById('studentId').value;
    const name = document.getElementById('studentName').value;
    const phone = document.getElementById('studentPhone').value;
    const type = document.getElementById('studentType').value;

    if (!name || !phone) {
        showToast('错误', '请填写所有必填字段', true);
        return;
    }

    const url = id ? `${API_BASE_URL}/api/students/${id}` : `${API_BASE_URL}/api/students`;
    const method = id ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, phone, type })
        });

        if (response.ok) {
            studentModal.hide();
            showToast('成功', id ? '学员更新成功' : '学员创建成功');
            loadStudents();
            loadDashboardData();
            loadStudentOptions();
        } else {
            const error = await response.json();
            showToast('错误', error.message || '操作失败', true);
        }
    } catch (error) {
        showToast('错误', '网络错误: ' + error.message, true);
    }
}

function showDeleteModal(id, type) {
    document.getElementById('deleteId').value = id;
    document.getElementById('deleteType').value = type;
    deleteModal.show();
}

async function confirmDelete() {
    const id = document.getElementById('deleteId').value;
    const type = document.getElementById('deleteType').value;
    const endpoint = type === 'appointment' ? 'appointments' : 'students';

    try {
        const response = await fetch(`${API_BASE_URL}/api/${endpoint}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            deleteModal.hide();
            showToast('成功', '删除成功');
            if (type === 'appointment') {
                loadAppointments();
            } else {
                loadStudents();
            }
            loadDashboardData();
        } else {
            showToast('错误', '删除失败', true);
        }
    } catch (error) {
        showToast('错误', '网络错误: ' + error.message, true);
    }
}

function getStatusText(status) {
    const statusMap = {
        'PENDING': '待确认',
        'CONFIRMED': '已确认',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
    };
    return statusMap[status] || status;
}

function getStatusColor(status) {
    const colorMap = {
        'PENDING': 'warning',
        'CONFIRMED': 'info',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
    };
    return colorMap[status] || 'secondary';
}

function getStudentTypeText(type) {
    const typeMap = {
        'REGULAR': '普通学员',
        'STUDENT': '学生学员',
        'GROUP': '团体学员'
    };
    return typeMap[type] || type;
}
