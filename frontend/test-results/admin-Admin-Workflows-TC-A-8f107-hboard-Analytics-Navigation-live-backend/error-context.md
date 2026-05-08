# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: admin.spec.js >> Admin Workflows >> TC-ADMIN-01: Admin Dashboard & Analytics Navigation
- Location: tests\e2e\admin.spec.js:26:3

# Error details

```
Error: Console errors detected during test: 
Failed to load resource: the server responded with a status of 500 (Internal Server Error)
Failed to load resource: the server responded with a status of 500 (Internal Server Error)
Failed to fetch carrier approved claims AxiosError: Request failed with status code 500
    at settle (http://localhost:3000/node_modules/.vite/deps/axios.js?v=9fec0096:1358:7)
    at XMLHttpRequest.onloadend (http://localhost:3000/node_modules/.vite/deps/axios.js?v=9fec0096:1742:7)
Failed to load resource: the server responded with a status of 500 (Internal Server Error)
Failed to load resource: the server responded with a status of 500 (Internal Server Error)
Failed to fetch carrier approved claims AxiosError: Request failed with status code 500
    at settle (http://localhost:3000/node_modules/.vite/deps/axios.js?v=9fec0096:1358:7)
    at XMLHttpRequest.onloadend (http://localhost:3000/node_modules/.vite/deps/axios.js?v=9fec0096:1742:7)
```

# Page snapshot

```yaml
- generic [ref=e1]:
  - generic [ref=e3]:
    - complementary [ref=e4]:
      - generic [ref=e6]:
        - generic [ref=e8]: T
        - generic [ref=e9]:
          - paragraph [ref=e10]: TPA ClaimSys
          - paragraph [ref=e11]: Insurance Platform
      - generic [ref=e12]:
        - generic [ref=e14]: M
        - generic [ref=e15]:
          - paragraph [ref=e16]: mithun
          - paragraph [ref=e17]: FMG_ADMIN
      - navigation [ref=e18]:
        - paragraph [ref=e19]: Navigation
        - link "Dashboard" [ref=e20] [cursor=pointer]:
          - /url: /dashboard
          - img [ref=e21]
          - generic [ref=e26]: Dashboard
        - link "Profile" [ref=e27] [cursor=pointer]:
          - /url: /profile
          - img [ref=e28]
          - generic [ref=e31]: Profile
        - link "Admin Panel Admin" [ref=e32] [cursor=pointer]:
          - /url: /admin
          - img [ref=e33]
          - generic [ref=e36]: Admin Panel
          - generic [ref=e37]: Admin
        - link "Fraud Dashboard Admin" [ref=e38] [cursor=pointer]:
          - /url: /admin/fraud-dashboard
          - img [ref=e39]
          - generic [ref=e41]: Fraud Dashboard
          - generic [ref=e42]: Admin
        - link "Analytics" [active] [ref=e43] [cursor=pointer]:
          - /url: /analytics
          - img [ref=e44]
          - generic [ref=e46]: Analytics
          - img [ref=e47]
      - button "Sign Out" [ref=e50] [cursor=pointer]:
        - img [ref=e51]
        - text: Sign Out
    - generic [ref=e54]:
      - banner [ref=e55]:
        - paragraph [ref=e57]: Welcome back, mithun
        - generic [ref=e58]:
          - generic [ref=e59]: FMG_ADMIN
          - button [ref=e61] [cursor=pointer]:
            - img [ref=e62]
          - button "Change Password" [ref=e65] [cursor=pointer]:
            - img [ref=e66]
      - main [ref=e69]:
        - generic [ref=e71]:
          - generic [ref=e72]:
            - heading "Platform Analytics" [level=1] [ref=e73]
            - paragraph [ref=e74]: Real-time insights and KPIs for the TPA system
          - generic [ref=e75]:
            - generic [ref=e76]:
              - img [ref=e78]
              - generic [ref=e80]:
                - paragraph [ref=e81]: Total Payout (Approved)
                - paragraph [ref=e82]: $0.00
            - generic [ref=e83]:
              - img [ref=e85]
              - generic [ref=e87]:
                - paragraph [ref=e88]: Total Claims
                - paragraph [ref=e89]: "5"
            - generic [ref=e90]:
              - img [ref=e92]
              - generic [ref=e95]:
                - paragraph [ref=e96]: Avg Processing Time
                - paragraph [ref=e97]: ~1.2 days
            - generic [ref=e98]:
              - img [ref=e100]
              - generic [ref=e103]:
                - paragraph [ref=e104]: Approval Rate
                - paragraph [ref=e105]: 0%
          - generic [ref=e106]:
            - generic [ref=e107]:
              - heading "Claims Submitted (Last 30 Days)" [level=2] [ref=e108]
              - application [ref=e112]:
                - generic [ref=e123]:
                  - generic [ref=e124]:
                    - generic [ref=e126]: 2026-05-07
                    - generic [ref=e128]: 2026-05-08
                  - generic [ref=e129]:
                    - generic [ref=e131]: "0"
                    - generic [ref=e133]: "1"
                    - generic [ref=e135]: "2"
                    - generic [ref=e137]: "3"
                    - generic [ref=e139]: "4"
            - generic [ref=e140]:
              - heading "Claim Status Distribution" [level=2] [ref=e141]
              - application [ref=e145]
              - generic [ref=e146]:
                - generic [ref=e149]: SUBMITTED (2)
                - generic [ref=e152]: UNDER_REVIEW (3)
  - generic [ref=e153]: "0"
```