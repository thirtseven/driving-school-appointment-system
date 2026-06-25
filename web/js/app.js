const API_BASE_URL = window.location.origin;

document.addEventListener('DOMContentLoaded', function() {
    loadDashboardData();
    loadAppointments();
    loadStudents();
});

function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    document.getElementById(sectionId).classList.add('active');
    event.target.closest('.nav-link').classList.add('active');
}

async function loadDashboardData() {
    try {
        const statsResponse = await fetch(`${API_BASE_URL}/api/statistics`);
        const stats = await statsResponse.json();

        document.getElementById('totalAppointments').textContent = stats.totalAppointments || 0;
        document.getElementById('totalRevenue').textContent = `¥${stats.totalRevenue || 0}`;

        if (stats.courseStats) {
            renderCourseChart(stats.courseStats);
        }

        const studentsResponse = await fetch(`${API_BASE_URL}/api/students`);
        const studentsData = await studentsResponse.json();
        document.getElementById('totalStudents').textContent = studentsData.count || 0;
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

function renderCourseChart(courseStats) {
    const ctx = document.getElementById('courseChart').getContext('2d');

    if (window.courseChartInstance) {
        window.courseChartInstance.destroy();
    }

    const labels = Object.keys(courseStats);
    const data = Object.values(courseStats);
    const colors = ['#0d6efd', '#198754', '#ffc107'];

    window.courseChartInstance = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                hoverBackgroundColor: colors.map(c => c + 'cc')
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
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
                    <td><span class="status-badge status-${apt.status}">${getStatusText(apt.status)}</span></td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="6" class="text-center">暂无预约记录</td></tr>';
        }
    } catch (error) {
        console.error('Error loading appointments:', error);
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
                    <td>${getDiscountText(student.type)}</td>
                `;
                tableBody.appendChild(row);
            });
        }
    } catch (error) {
        console.error('Error loading students:', error);
    }
}

function getStudentTypeText(type) {
    const typeMap = {
        'REGULAR': '普通学员',
        'STUDENT': '学生学员',
        'GROUP': '团体学员'
    };
    return typeMap[type] || type;
}

function getDiscountText(type) {
    const discountMap = {
        'REGULAR': '原价',
        'STUDENT': '8折',
        'GROUP': '7折'
    };
    return discountMap[type] || '原价';
}

async function runCDPTest(testType) {
    const resultsDiv = document.getElementById('testResults');
    resultsDiv.innerHTML = `<span class="info">[${new Date().toLocaleTimeString()}] 开始执行 ${testType} 测试...</span>\n\n`;

    try {
        let results;

        switch (testType) {
            case 'page_load':
                results = await testPageLoad();
                break;
            case 'api_test':
                results = await testAPIs();
                break;
            case 'ui_test':
                results = await testUI();
                break;
            case 'full_test':
                results = await runFullTest();
                break;
        }

        results.forEach(r => {
            const statusClass = r.status === 'PASS' ? 'success' : 'error';
            resultsDiv.innerHTML += `<span class="${statusClass}">[${r.status}]</span> ${r.message}\n`;
        });

    } catch (error) {
        resultsDiv.innerHTML += `<span class="error">[ERROR] 测试执行失败: ${error.message}</span>\n`;
    }
}

async function testPageLoad() {
    const results = [];

    try {
        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/`);
        const loadTime = performance.now() - start;

        results.push({
            status: response.ok ? 'PASS' : 'FAIL',
            message: `页面加载测试 - 状态码: ${response.status} (${loadTime.toFixed(2)}ms)`
        });
    } catch (error) {
        results.push({ status: 'FAIL', message: `页面加载失败: ${error.message}` });
    }

    return results;
}

async function testAPIs() {
    const results = [];
    const endpoints = [
        { url: '/api/appointments', name: '预约API' },
        { url: '/api/courses', name: '课程API' },
        { url: '/api/students', name: '学员API' },
        { url: '/api/statistics', name: '统计API' }
    ];

    for (const endpoint of endpoints) {
        try {
            const start = performance.now();
            const response = await fetch(`${API_BASE_URL}${endpoint.url}`);
            const data = await response.json();
            const loadTime = performance.now() - start;

            results.push({
                status: response.ok ? 'PASS' : 'FAIL',
                message: `${endpoint.name} (${endpoint.url}) - 状态码: ${response.status} (${loadTime.toFixed(2)}ms)`
            });
        } catch (error) {
            results.push({ status: 'FAIL', message: `${endpoint.name} 测试失败: ${error.message}` });
        }
    }

    return results;
}

async function testUI() {
    const results = [];

    const navItems = document.querySelectorAll('.nav-item');
    results.push({
        status: navItems.length >= 4 ? 'PASS' : 'FAIL',
        message: `导航栏元素检查 - 找到 ${navItems.length} 个导航项`
    });

    const cards = document.querySelectorAll('.card');
    results.push({
        status: cards.length > 0 ? 'PASS' : 'FAIL',
        message: `卡片组件检查 - 找到 ${cards.length} 个卡片`
    });

    const sections = document.querySelectorAll('.section');
    results.push({
        status: sections.length >= 4 ? 'PASS' : 'FAIL',
        message: `页面区块检查 - 找到 ${sections.length} 个区块`
    });

    return results;
}

async function runFullTest() {
    const allResults = [];

    const pageResults = await testPageLoad();
    allResults.push(...pageResults);

    const apiResults = await testAPIs();
    allResults.push(...apiResults);

    const uiResults = await testUI();
    allResults.push(...uiResults);

    const passCount = allResults.filter(r => r.status === 'PASS').length;
    const failCount = allResults.filter(r => r.status === 'FAIL').length;

    allResults.push({
        status: failCount === 0 ? 'PASS' : 'FAIL',
        message: `测试汇总 - 通过: ${passCount}, 失败: ${failCount}, 总计: ${allResults.length}`
    });

    return allResults;
}

function clearTestResults() {
    document.getElementById('testResults').innerHTML = '等待测试执行...';
}

function showAddAppointmentModal() {
    alert('新增预约功能开发中...');
}
