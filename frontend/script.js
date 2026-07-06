// ================= LOGIN =================

async function checkLogin() {
    try {

        let email = document.getElementById("email").value;
        let password = document.getElementById("password").value;

        let response = await fetch(
            `http://localhost:8080/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
        );

        let result = await response.text();

        console.log("Server Response:", result);

        let data = result.trim().split("|");

        if (data[0] === "success") {

            localStorage.setItem("userId", data[1]);
            localStorage.setItem("userName", data[2]);
            localStorage.setItem("userEmail", data[3]);
            localStorage.setItem("teachingSkill", data[4]);
            localStorage.setItem("learningSkill", data[5]);

            alert("Login Successful!");

            window.location.href = "dashboard.html";

        } else {

            alert("Invalid Email or Password!");

        }

    } catch (error) {

        console.error(error);
        alert("Cannot connect to backend server!");

    }
}

// ================= REGISTER =================

async function registerUser() {
    try {

        let name = document.getElementById("regName").value;
        let email = document.getElementById("regEmail").value;
        let password = document.getElementById("regPassword").value;
        let teachingSkill = document.getElementById("teachingSkill").value;
        let learningSkill = document.getElementById("learningSkill").value;

        if (
            name === "" ||
            email === "" ||
            password === "" ||
            teachingSkill === "" ||
            learningSkill === ""
        ) {
            alert("Please fill all fields!");
            return;
        }

        let response = await fetch(
            `http://localhost:8080/register?name=${encodeURIComponent(name)}&email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}&teachingSkill=${encodeURIComponent(teachingSkill)}&learningSkill=${encodeURIComponent(learningSkill)}`
        );

        let result = await response.text();

        if (result.trim() === "registered") {

            alert("Registration Successful!");
            window.location.href = "login.html";

        } else if (result.trim() === "email_exists") {

            alert("Email already registered!");

        } else {

            alert("Registration Failed!");

        }

    } catch (error) {

        console.error(error);
        alert("Cannot connect to backend server!");

    }
}

// ================= SKILLS SEARCH =================

function searchSkills() {

    let input = document.getElementById("searchInput").value.toLowerCase();

    let cards = document.querySelectorAll(".skill-card");

    cards.forEach(card => {

        let skill = card.getAttribute("data-skill");

        if (skill.includes(input)) {

            card.style.display = "block";

        } else {

            card.style.display = "none";

        }

    });

}

// ================= REQUESTS =================

async function sendRequest(skillName, receiverId) {

    let senderId = localStorage.getItem("userId");

    try {

        let response = await fetch(
            `http://localhost:8080/sendRequest?senderId=${senderId}&receiverId=${receiverId}&skill=${encodeURIComponent(skillName)}`
        );

        let result = await response.text();

        if (result.trim() === "request_sent") {

            alert("Request Sent Successfully!");

        } else if (result.trim() === "already_sent") {

            alert("Request Already Sent!");

        } else if (result.trim() === "self_request") {

            alert("You cannot send request to yourself!");

        } else {

            alert("Something went wrong!");

        }

    } catch (error) {

        console.log(error);
        alert("Cannot connect to backend!");

    }
}


function acceptRequest() {

    let status = document.getElementById("status1");

    if (status) {

        status.innerHTML = "Accepted";

    }

}

function rejectRequest() {

    let request = document.getElementById("request1");

    if (request) {

        request.remove();

    }

}
async function loadRequests() {
    let userId = localStorage.getItem("userId");

    let response = await fetch(
        `http://localhost:8080/myRequests?userId=${userId}`
    );

    let result = await response.text();

    let table = document.getElementById("requestsTable");
    table.innerHTML = "";

    if (result.trim() === "") {
        table.innerHTML = "<tr><td colspan='5'>No requests found</td></tr>";
        return;
    }

    let rows = result.trim().split("\n");

    rows.forEach(row => {
        let data = row.split("|");

        let requestId = data[0];
        let type = data[1];
        let skill = data[2];
        let student = data[3];
        let status = data[4];

        let action = "-";

        if (type === "Incoming" && status === "Pending") {
            action = `
                <button class="accept-btn" onclick="acceptRequest(${requestId})">Accept</button>
                <button class="reject-btn" onclick="rejectRequest(${requestId})">Reject</button>
            `;
        }

        table.innerHTML += `
            <tr>
                <td>${type}</td>
                <td>${skill}</td>
                <td>${student}</td>
                <td>${status}</td>
                <td>${action}</td>
            </tr>
        `;
    });
}

async function acceptRequest(requestId) {
    await fetch(`http://localhost:8080/acceptRequest?id=${requestId}`);
    alert("Request Accepted");
    loadRequests();
}

async function rejectRequest(requestId) {
    await fetch(`http://localhost:8080/rejectRequest?id=${requestId}`);
    alert("Request Rejected");
    loadRequests();
}

