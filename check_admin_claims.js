async function test() {
    try {
        const loginRes = await fetch('http://localhost:8081/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: 'mithun-io@outlook.com',
                password: 'Qw3!@sPe:E1'
            })
        });
        const loginText = await loginRes.text();
        const loginData = JSON.parse(loginText);
        const token = loginData.accessToken;
        
        console.log("Got token.");
        const claimsRes = await fetch('http://localhost:8081/api/v1/claims/search?page=0&size=10&sort=createdDate,desc', {
            headers: { Authorization: `Bearer ${token}` }
        });
        const claimsText = await claimsRes.text();
        console.log("Status:", claimsRes.status);
        console.log("Response:", claimsText);
    } catch (err) {
        console.error(err);
    }
}
test();
