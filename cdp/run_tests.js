const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

const BASE_URL = 'http://localhost:8080';
const RESULTS_DIR = './results';

class CDPTestRunner {
    constructor() {
        this.browser = null;
        this.page = null;
        this.results = [];
    }

    async init() {
        if (!fs.existsSync(RESULTS_DIR)) {
            fs.mkdirSync(RESULTS_DIR, { recursive: true });
        }

        this.browser = await puppeteer.launch({
            headless: 'new',
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });
        this.page = await this.browser.newPage();
        await this.page.setViewport({ width: 1280, height: 800 });
    }

    async close() {
        if (this.browser) {
            await this.browser.close();
        }
    }

    log(message, status = 'INFO') {
        const timestamp = new Date().toISOString();
        const logEntry = `[${timestamp}] [${status}] ${message}`;
        console.log(logEntry);
        this.results.push({ timestamp, status, message });
    }

    async testPageLoad() {
        this.log('Starting page load test...');

        try {
            const startTime = Date.now();
            await this.page.goto(BASE_URL, { waitUntil: 'networkidle0' });
            const loadTime = Date.now() - startTime;

            const title = await this.page.title();
            this.log(`Page loaded successfully - Title: "${title}" - Load time: ${loadTime}ms`, 'PASS');

            const navItems = await this.page.$$('.nav-item');
            this.log(`Navigation items found: ${navItems.length}`, navItems.length >= 4 ? 'PASS' : 'FAIL');

            return true;
        } catch (error) {
            this.log(`Page load failed: ${error.message}`, 'FAIL');
            return false;
        }
    }

    async testDashboard() {
        this.log('Testing dashboard section...');

        try {
            await this.page.click('[onclick="showSection(\'dashboard\')"]');
            await this.page.waitForSelector('#dashboard.active');

            const statsCards = await this.page.$$('#dashboard .card');
            this.log(`Dashboard cards found: ${statsCards.length}`, statsCards.length >= 4 ? 'PASS' : 'FAIL');

            const totalAppointments = await this.page.$eval('#totalAppointments', el => el.textContent);
            this.log(`Total appointments displayed: ${totalAppointments}`, 'PASS');

            return true;
        } catch (error) {
            this.log(`Dashboard test failed: ${error.message}`, 'FAIL');
            return false;
        }
    }

    async testAppointments() {
        this.log('Testing appointments section...');

        try {
            await this.page.click('[onclick="showSection(\'appointments\')"]');
            await this.page.waitForSelector('#appointments.active');

            const tableRows = await this.page.$$('#appointmentsTable tr');
            this.log(`Appointment rows found: ${tableRows.length}`, 'PASS');

            return true;
        } catch (error) {
            this.log(`Appointments test failed: ${error.message}`, 'FAIL');
            return false;
        }
    }

    async testCourses() {
        this.log('Testing courses section...');

        try {
            await this.page.click('[onclick="showSection(\'courses\')"]');
            await this.page.waitForSelector('#courses.active');

            const courseCards = await this.page.$$('#courses .card');
            this.log(`Course cards found: ${courseCards.length}`, courseCards.length >= 3 ? 'PASS' : 'FAIL');

            return true;
        } catch (error) {
            this.log(`Courses test failed: ${error.message}`, 'FAIL');
            return false;
        }
    }

    async testStudents() {
        this.log('Testing students section...');

        try {
            await this.page.click('[onclick="showSection(\'students\')"]');
            await this.page.waitForSelector('#students.active');

            const studentRows = await this.page.$$('#studentsTable tr');
            this.log(`Student rows found: ${studentRows.length}`, studentRows.length > 0 ? 'PASS' : 'FAIL');

            return true;
        } catch (error) {
            this.log(`Students test failed: ${error.message}`, 'FAIL');
            return false;
        }
    }

    async testAPIEndpoints() {
        this.log('Testing API endpoints...');

        const endpoints = [
            { url: '/api/appointments', name: 'Appointments API' },
            { url: '/api/courses', name: 'Courses API' },
            { url: '/api/students', name: 'Students API' },
            { url: '/api/statistics', name: 'Statistics API' }
        ];

        for (const endpoint of endpoints) {
            try {
                const response = await this.page.evaluate(async (url) => {
                    const res = await fetch(url);
                    return { status: res.status, ok: res.ok };
                }, endpoint.url);

                this.log(
                    `${endpoint.name} - Status: ${response.status}`,
                    response.ok ? 'PASS' : 'FAIL'
                );
            } catch (error) {
                this.log(`${endpoint.name} failed: ${error.message}`, 'FAIL');
            }
        }

        return true;
    }

    async testNavigation() {
        this.log('Testing navigation functionality...');

        const sections = ['dashboard', 'appointments', 'courses', 'students', 'test'];

        for (const section of sections) {
            try {
                await this.page.click(`[onclick="showSection('${section}')"]`);
                await this.page.waitForSelector(`#${section}.active`);
                this.log(`Navigation to ${section} - OK`, 'PASS');
            } catch (error) {
                this.log(`Navigation to ${section} failed: ${error.message}`, 'FAIL');
            }
        }

        return true;
    }

    async runAllTests() {
        this.log('========================================');
        this.log('  CDP自动化测试开始');
        this.log('========================================\n');

        await this.init();

        const tests = [
            { name: 'Page Load', fn: () => this.testPageLoad() },
            { name: 'Dashboard', fn: () => this.testDashboard() },
            { name: 'Navigation', fn: () => this.testNavigation() },
            { name: 'Appointments', fn: () => this.testAppointments() },
            { name: 'Courses', fn: () => this.testCourses() },
            { name: 'Students', fn: () => this.testStudents() },
            { name: 'API Endpoints', fn: () => this.testAPIEndpoints() }
        ];

        let passed = 0;
        let failed = 0;

        for (const test of tests) {
            this.log(`\nRunning: ${test.name}`);
            try {
                const result = await test.fn();
                if (result) passed++;
                else failed++;
            } catch (error) {
                this.log(`Test ${test.name} threw an error: ${error.message}`, 'FAIL');
                failed++;
            }
        }

        this.log('\n========================================');
        this.log(`  测试完成 - 通过: ${passed}, 失败: ${failed}`);
        this.log('========================================');

        await this.saveResults();
        await this.close();

        return { passed, failed, total: passed + failed };
    }

    async saveResults() {
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
        const filename = path.join(RESULTS_DIR, `test_results_${timestamp}.json`);

        const report = {
            timestamp: new Date().toISOString(),
            baseUrl: BASE_URL,
            results: this.results,
            summary: {
                total: this.results.filter(r => r.status === 'PASS' || r.status === 'FAIL').length,
                passed: this.results.filter(r => r.status === 'PASS').length,
                failed: this.results.filter(r => r.status === 'FAIL').length
            }
        };

        fs.writeFileSync(filename, JSON.stringify(report, null, 2));
        this.log(`Results saved to: ${filename}`);
    }
}

async function main() {
    const runner = new CDPTestRunner();
    try {
        const summary = await runner.runAllTests();
        process.exit(summary.failed > 0 ? 1 : 0);
    } catch (error) {
        console.error('Test runner failed:', error);
        process.exit(1);
    }
}

main();
