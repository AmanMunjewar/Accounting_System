package com.vectorbank.web;

/**
 * Modern, responsive single-page Web UI for VECTOR Bank.
 * Bundled directly in Java with no external static asset dependencies.
 */
public class WebUiTemplate {

    public static String getHtml() {
        return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VECTOR Bank - Precision Financial Banking</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg-primary: #0a0e17;
            --bg-secondary: #121927;
            --bg-card: #182235;
            --accent-blue: #2563eb;
            --accent-cyan: #06b6d4;
            --accent-green: #10b981;
            --accent-red: #ef4444;
            --text-main: #f8fafc;
            --text-muted: #94a3b8;
            --border-color: #2a374f;
            --radius: 12px;
            --shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.4), 0 8px 10px -6px rgba(0, 0, 0, 0.4);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Inter', sans-serif;
        }

        body {
            background-color: var(--bg-primary);
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        header {
            background: rgba(18, 25, 39, 0.85);
            backdrop-filter: blur(12px);
            border-bottom: 1px solid var(--border-color);
            padding: 1rem 2rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 50;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 800;
            font-size: 1.3rem;
            letter-spacing: -0.5px;
            background: linear-gradient(135deg, #60a5fa, #06b6d4);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .brand-badge {
            font-size: 0.7rem;
            padding: 3px 8px;
            border-radius: 20px;
            background: rgba(37, 99, 235, 0.2);
            border: 1px solid rgba(96, 165, 250, 0.4);
            color: #93c5fd;
            -webkit-text-fill-color: #93c5fd;
            font-weight: 600;
            letter-spacing: 0.5px;
        }

        .auth-status {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        main {
            flex: 1;
            max-width: 1200px;
            width: 100%;
            margin: 0 auto;
            padding: 2rem;
        }

        .card {
            background: var(--bg-secondary);
            border: 1px solid var(--border-color);
            border-radius: var(--radius);
            padding: 1.75rem;
            box-shadow: var(--shadow);
            margin-bottom: 1.5rem;
        }

        .grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }

        @media (max-width: 768px) {
            .grid-2 { grid-template-columns: 1fr; }
            main { padding: 1rem; }
        }

        h2 {
            font-size: 1.25rem;
            font-weight: 700;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 8px;
            color: #f1f5f9;
        }

        label {
            display: block;
            font-size: 0.85rem;
            font-weight: 500;
            color: var(--text-muted);
            margin-bottom: 0.35rem;
        }

        input {
            width: 100%;
            padding: 0.75rem 1rem;
            background: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            color: #fff;
            font-size: 0.95rem;
            margin-bottom: 1rem;
            transition: all 0.2s;
        }

        input:focus {
            outline: none;
            border-color: var(--accent-cyan);
            box-shadow: 0 0 0 3px rgba(6, 182, 212, 0.2);
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            width: 100%;
            padding: 0.75rem 1.25rem;
            font-size: 0.95rem;
            font-weight: 600;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn-primary {
            background: linear-gradient(135deg, var(--accent-blue), #1d4ed8);
            color: #fff;
        }
        .btn-primary:hover {
            opacity: 0.95;
            transform: translateY(-1px);
        }

        .btn-success {
            background: linear-gradient(135deg, var(--accent-green), #059669);
            color: #fff;
        }
        .btn-danger {
            background: linear-gradient(135deg, var(--accent-red), #dc2626);
            color: #fff;
        }
        .btn-secondary {
            background: var(--bg-card);
            color: var(--text-main);
            border: 1px solid var(--border-color);
        }
        .btn-secondary:hover {
            background: #202d45;
        }

        /* Banner balance card */
        .balance-hero {
            background: linear-gradient(135deg, #1e293b, #0f172a);
            border: 1px solid rgba(96, 165, 250, 0.3);
            border-radius: var(--radius);
            padding: 2rem;
            margin-bottom: 1.5rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: relative;
            overflow: hidden;
        }

        .balance-hero::before {
            content: '';
            position: absolute;
            top: -50px;
            right: -50px;
            width: 150px;
            height: 150px;
            background: radial-gradient(circle, rgba(6, 182, 212, 0.25) 0%, transparent 70%);
            border-radius: 50%;
        }

        .balance-amount {
            font-size: 2.75rem;
            font-weight: 800;
            font-family: 'JetBrains Mono', monospace;
            background: linear-gradient(135deg, #ffffff, #cbd5e1);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin: 0.5rem 0;
        }

        .user-tag {
            color: var(--text-muted);
            font-size: 0.9rem;
        }

        .pill {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 0.75rem;
            font-weight: 600;
            font-family: 'JetBrains Mono', monospace;
        }
        .pill-deposit { background: rgba(16, 185, 129, 0.15); color: #34d399; }
        .pill-withdraw { background: rgba(239, 68, 68, 0.15); color: #f87171; }

        /* Tables */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
        }

        th {
            text-align: left;
            padding: 0.75rem 1rem;
            color: var(--text-muted);
            font-weight: 600;
            border-bottom: 1px solid var(--border-color);
            background: rgba(24, 34, 53, 0.5);
        }

        td {
            padding: 0.85rem 1rem;
            border-bottom: 1px solid rgba(42, 55, 79, 0.5);
            font-family: 'JetBrains Mono', monospace;
        }

        tr:hover td {
            background: rgba(255, 255, 255, 0.02);
        }

        .alert {
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1rem;
            font-size: 0.9rem;
            display: none;
        }
        .alert-error {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.3);
            color: #fca5a5;
        }
        .alert-success {
            background: rgba(16, 185, 129, 0.15);
            border: 1px solid rgba(16, 185, 129, 0.3);
            color: #6ee7b7;
        }

        .hidden { display: none !important; }
    </style>
</head>
<body>

    <header>
        <div class="brand">
            🏦 VECTOR Bank
            <span class="brand-badge">ACID • O(1) LRU • Min-Heap</span>
        </div>
        <div id="header-auth" class="auth-status">
            <span id="header-user" class="user-tag hidden"></span>
            <button id="btn-logout" class="btn btn-secondary hidden" style="width: auto; padding: 0.5rem 1rem;" onclick="logout()">Logout</button>
        </div>
    </header>

    <main>
        <!-- Alerts -->
        <div id="global-alert" class="alert"></div>

        <!-- AUTH VIEW (Login / Create Account) -->
        <div id="view-auth">
            <div class="grid-2">
                <!-- Login Card -->
                <div class="card">
                    <h2>🔑 Sign In to Existing Account</h2>
                    <form onsubmit="handleLogin(event)">
                        <div>
                            <label>Account Number</label>
                            <input type="number" id="login-acc" placeholder="e.g. 1001" required>
                        </div>
                        <div>
                            <label>Password</label>
                            <input type="password" id="login-pass" placeholder="••••••••" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Sign In</button>
                    </form>
                </div>

                <!-- Create Account Card -->
                <div class="card">
                    <h2>✨ Open New Bank Account</h2>
                    <form onsubmit="handleCreateAccount(event)">
                        <div>
                            <label>Full Name</label>
                            <input type="text" id="reg-name" placeholder="John Doe" required>
                        </div>
                        <div>
                            <label>Date of Birth</label>
                            <input type="date" id="reg-dob" required>
                        </div>
                        <div>
                            <label>PAN / Tax ID</label>
                            <input type="text" id="reg-pan" placeholder="ABCDE1234F" required>
                        </div>
                        <div>
                            <label>Initial Deposit ($)</label>
                            <input type="number" step="0.01" min="0" id="reg-deposit" placeholder="500.00" required>
                        </div>
                        <div>
                            <label>Account Password</label>
                            <input type="password" id="reg-pass" placeholder="••••••••" required>
                        </div>
                        <button type="submit" class="btn btn-success">Open Account</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- DASHBOARD VIEW -->
        <div id="view-dashboard" class="hidden">
            <!-- Balance Banner -->
            <div class="balance-hero">
                <div>
                    <span class="user-tag" id="dash-greeting">Account #1001</span>
                    <div class="balance-amount" id="dash-balance">$0.00</div>
                    <span class="pill pill-deposit" id="dash-pan">PAN: ---</span>
                </div>
                <div style="text-align: right;">
                    <div class="user-tag">Date of Birth</div>
                    <div style="font-weight: 600; font-size: 1.1rem;" id="dash-dob">YYYY-MM-DD</div>
                </div>
            </div>

            <!-- Transaction Actions -->
            <div class="grid-2">
                <!-- Deposit -->
                <div class="card">
                    <h2>📥 Deposit Funds</h2>
                    <form onsubmit="handleDeposit(event)">
                        <div>
                            <label>Amount ($)</label>
                            <input type="number" step="0.01" min="0.01" id="dep-amount" placeholder="100.00" required>
                        </div>
                        <button type="submit" class="btn btn-success">Deposit (Atomic ACID)</button>
                    </form>
                </div>

                <!-- Withdraw -->
                <div class="card">
                    <h2>📤 Withdraw Funds</h2>
                    <form onsubmit="handleWithdraw(event)">
                        <div>
                            <label>Amount ($)</label>
                            <input type="number" step="0.01" min="0.01" id="with-amount" placeholder="50.00" required>
                        </div>
                        <button type="submit" class="btn btn-danger">Withdraw (Overdraft Protected)</button>
                    </form>
                </div>
            </div>

            <!-- Passbook and Top-K DSA -->
            <div class="card">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; gap: 10px;">
                    <h2>📜 Passbook History</h2>
                    <div style="display: flex; gap: 10px; align-items: center;">
                        <input type="number" id="top-k-val" value="3" min="1" max="50" style="width: 70px; margin-bottom: 0; padding: 0.5rem;">
                        <button class="btn btn-secondary" style="width: auto; padding: 0.5rem 1rem;" onclick="loadTopK()">⚡ Top-K Min-Heap</button>
                        <button class="btn btn-secondary" style="width: auto; padding: 0.5rem 1rem;" onclick="loadPassbook()">All Transactions</button>
                    </div>
                </div>
                <div style="overflow-x: auto;">
                    <table>
                        <thead>
                            <tr id="table-header">
                                <th>Tx ID</th>
                                <th>Timestamp</th>
                                <th>Deposit</th>
                                <th>Withdraw</th>
                                <th>Balance After</th>
                            </tr>
                        </thead>
                        <tbody id="tx-table-body">
                            <tr><td colspan="5" style="text-align: center; color: var(--text-muted);">No records loaded.</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>

    <script>
        let currentAccount = null;

        function showAlert(msg, isSuccess = true) {
            const el = document.getElementById('global-alert');
            el.className = 'alert ' + (isSuccess ? 'alert-success' : 'alert-error');
            el.innerText = msg;
            el.style.display = 'block';
            setTimeout(() => { el.style.display = 'none'; }, 6000);
        }

        async function handleLogin(e) {
            e.preventDefault();
            const acc = document.getElementById('login-acc').value;
            const pass = document.getElementById('login-pass').value;

            try {
                const res = await fetch('/api/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `accountNo=${encodeURIComponent(acc)}&password=${encodeURIComponent(pass)}`
                });
                const data = await res.json();
                if (data.success) {
                    currentAccount = data;
                    renderDashboard();
                    showAlert(`Welcome back, ${data.name}!`);
                } else {
                    showAlert(data.error || 'Login failed', false);
                }
            } catch (err) {
                showAlert('Network error: ' + err.message, false);
            }
        }

        async function handleCreateAccount(e) {
            e.preventDefault();
            const name = document.getElementById('reg-name').value;
            const dob = document.getElementById('reg-dob').value;
            const pan = document.getElementById('reg-pan').value;
            const deposit = document.getElementById('reg-deposit').value;
            const pass = document.getElementById('reg-pass').value;

            try {
                const res = await fetch('/api/create-account', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `name=${encodeURIComponent(name)}&dob=${encodeURIComponent(dob)}&pan=${encodeURIComponent(pan)}&initialDeposit=${encodeURIComponent(deposit)}&password=${encodeURIComponent(pass)}`
                });
                const data = await res.json();
                if (data.success) {
                    showAlert(`🎉 Account successfully created! Your Account Number is [${data.accountNo}]. Please log in.`);
                    document.getElementById('login-acc').value = data.accountNo;
                } else {
                    showAlert(data.error || 'Registration failed', false);
                }
            } catch (err) {
                showAlert('Network error: ' + err.message, false);
            }
        }

        function renderDashboard() {
            document.getElementById('view-auth').classList.add('hidden');
            document.getElementById('view-dashboard').classList.remove('hidden');

            document.getElementById('header-user').innerText = `👤 ${currentAccount.name} (#${currentAccount.accountNo})`;
            document.getElementById('header-user').classList.remove('hidden');
            document.getElementById('btn-logout').classList.remove('hidden');

            document.getElementById('dash-greeting').innerText = `${currentAccount.name} • Account #${currentAccount.accountNo}`;
            document.getElementById('dash-balance').innerText = `$${parseFloat(currentAccount.balance).toFixed(2)}`;
            document.getElementById('dash-pan').innerText = `PAN: ${currentAccount.pan}`;
            document.getElementById('dash-dob').innerText = currentAccount.dob;

            loadPassbook();
        }

        function logout() {
            currentAccount = null;
            document.getElementById('view-dashboard').classList.add('hidden');
            document.getElementById('view-auth').classList.remove('hidden');
            document.getElementById('header-user').classList.add('hidden');
            document.getElementById('btn-logout').classList.add('hidden');
            showAlert('You have been logged out.');
        }

        async function handleDeposit(e) {
            e.preventDefault();
            const amt = document.getElementById('dep-amount').value;
            try {
                const res = await fetch('/api/deposit', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `accountNo=${currentAccount.accountNo}&amount=${encodeURIComponent(amt)}`
                });
                const data = await res.json();
                if (data.success) {
                    currentAccount.balance = data.newBalance;
                    document.getElementById('dash-balance').innerText = `$${parseFloat(data.newBalance).toFixed(2)}`;
                    document.getElementById('dep-amount').value = '';
                    showAlert(`✅ Deposit of $${parseFloat(amt).toFixed(2)} successful!`);
                    loadPassbook();
                } else {
                    showAlert(data.error || 'Deposit failed', false);
                }
            } catch (err) {
                showAlert('Error: ' + err.message, false);
            }
        }

        async function handleWithdraw(e) {
            e.preventDefault();
            const amt = document.getElementById('with-amount').value;
            try {
                const res = await fetch('/api/withdraw', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: `accountNo=${currentAccount.accountNo}&amount=${encodeURIComponent(amt)}`
                });
                const data = await res.json();
                if (data.success) {
                    currentAccount.balance = data.newBalance;
                    document.getElementById('dash-balance').innerText = `$${parseFloat(data.newBalance).toFixed(2)}`;
                    document.getElementById('with-amount').value = '';
                    showAlert(`✅ Withdrawal of $${parseFloat(amt).toFixed(2)} successful!`);
                    loadPassbook();
                } else {
                    showAlert(`❌ ${data.error || 'Withdrawal rejected'}`, false);
                }
            } catch (err) {
                showAlert('Error: ' + err.message, false);
            }
        }

        async function loadPassbook() {
            try {
                const res = await fetch(`/api/passbook?accountNo=${currentAccount.accountNo}`);
                const data = await res.json();
                if (data.success) {
                    const tbody = document.getElementById('tx-table-body');
                    document.getElementById('table-header').innerHTML = `
                        <th>Tx ID</th><th>Timestamp</th><th>Deposit</th><th>Withdraw</th><th>Balance After</th>
                    `;
                    if (data.transactions.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No transaction history.</td></tr>';
                        return;
                    }
                    tbody.innerHTML = data.transactions.map(t => `
                        <tr>
                            <td>#${t.id}</td>
                            <td>${t.time}</td>
                            <td>${parseFloat(t.deposit) > 0 ? '<span class="pill pill-deposit">+$' + parseFloat(t.deposit).toFixed(2) + '</span>' : '-'}</td>
                            <td>${parseFloat(t.withdraw) > 0 ? '<span class="pill pill-withdraw">-$' + parseFloat(t.withdraw).toFixed(2) + '</span>' : '-'}</td>
                            <td><strong>$${parseFloat(t.balance).toFixed(2)}</strong></td>
                        </tr>
                    `).join('');
                }
            } catch (err) {
                console.error(err);
            }
        }

        async function loadTopK() {
            const k = document.getElementById('top-k-val').value || 3;
            try {
                const res = await fetch(`/api/top-k?accountNo=${currentAccount.accountNo}&k=${k}`);
                const data = await res.json();
                if (data.success) {
                    const tbody = document.getElementById('tx-table-body');
                    document.getElementById('table-header').innerHTML = `
                        <th>Rank</th><th>Type</th><th>Amount</th><th>Timestamp</th><th>Balance After</th>
                    `;
                    if (data.transactions.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">No transactions found.</td></tr>';
                        return;
                    }
                    tbody.innerHTML = data.transactions.map((t, idx) => `
                        <tr>
                            <td><strong>#${idx + 1}</strong></td>
                            <td><span class="pill ${t.type === 'DEPOSIT' ? 'pill-deposit' : 'pill-withdraw'}">${t.type}</span></td>
                            <td><strong>$${parseFloat(t.amount).toFixed(2)}</strong></td>
                            <td>${t.time}</td>
                            <td>$${parseFloat(t.balance).toFixed(2)}</td>
                        </tr>
                    `).join('');
                    showAlert(`⚡ Top ${data.transactions.length} largest transactions extracted via Min-Heap O(N log K)!`);
                }
            } catch (err) {
                console.error(err);
            }
        }
    </script>
</body>
</html>
        """;
    }
}
