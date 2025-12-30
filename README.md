<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HomeWay README</title>
    <style>
        .language-toggle {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 1000;
        }
        .language-toggle button {
            padding: 10px 20px;
            margin: 0 5px;
            cursor: pointer;
            border: 2px solid #333;
            background: white;
            border-radius: 5px;
            font-weight: bold;
        }
        .language-toggle button.active {
            background: #333;
            color: white;
        }
        .lang-content {
            display: none;
        }
        .lang-content.active {
            display: block;
        }
        .rtl {
            direction: rtl;
            text-align: right;
        }
    </style>
</head>
<body>

<div class="language-toggle">
    <button onclick="switchLanguage('en')" id="btn-en" class="active">English</button>
    <button onclick="switchLanguage('ar')" id="btn-ar">العربية</button>
</div>

<div id="content-en" class="lang-content active">

<h1 id="top">HomeWay – Property Services Management Platform</h1>

<p>
  <strong>HomeWay</strong> is a Spring Boot backend that connects customers with specialized service companies to manage residential properties efficiently.
  It supports end-to-end service lifecycles for <strong>Inspection</strong>, <strong>Maintenance</strong>, <strong>Moving</strong>, and <strong>Redesign</strong>,
  enforcing structured workflows, secure access control, transparent pricing, real-time notifications, payment processing, and AI-powered assistance.
</p>

<hr />

<h2>Table of Contents</h2>
<ul>
  <li><a href="#introduction">1. Introduction</a></li>
  <li><a href="#key-features">2. Key Features</a></li>
  <li><a href="#roles-permissions">3. User Roles &amp; Permissions</a></li>
  <li><a href="#architecture">4. Architecture</a></li>
  <li><a href="#core-workflows">5. Core Workflows</a></li>
  <li><a href="#problems-solved">6. Problems Solved</a></li>
  <li><a href="#contributing">7. Contributing</a></li>
  <li><a href="#contributors">8. Contributors</a></li>
</ul>

<hr />

<h2 id="introduction">1. Introduction</h2>
<p>
  HomeWay is designed as a realistic, production-style backend system. It integrates business rules, role-based authorization,
  payment verification, notifications, reporting, and subscription-gated AI features to enhance decision-making for customers
  and service providers.
</p>
<p>
  The platform centers around a controlled request lifecycle:
  <strong>Pending → Approved → In Progress → Completed</strong> (or <strong>Rejected</strong>),
  with strict validation of role permissions, ownership, and state transitions.
</p>
<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="key-features">2. Key Features</h2>

<h3>Property Management</h3>
<ul>
  <li>Customers can create, update, and manage multiple properties.</li>
  <li>All service requests are tied to a specific property for traceability.</li>
</ul>

<h3>Service Request Lifecycle</h3>
<ul>
  <li>Controlled flow: <strong>Pending → Approved → In Progress → Completed</strong> (or <strong>Rejected</strong>).</li>
  <li>Requests are explicitly linked to a company and a property.</li>
  <li>Strict validation of request state transitions and ownership checks.</li>
</ul>

<h3>Offer &amp; Payment System</h3>
<ul>
  <li>Companies approve requests by creating a pricing offer.</li>
  <li>Customers can accept or reject offers before payment.</li>
  <li>Secure payment processing via <strong>Moyasar</strong> with verification and automatic request payment updates.</li>
</ul>

<h3>Resource Management</h3>
<ul>
  <li>Automatic assignment of available workers for requests.</li>
  <li>Vehicle assignment for moving services.</li>
  <li>Availability tracking to prevent double-booking and conflicts.</li>
</ul>

<h3>Reports &amp; Reviews</h3>
<ul>
  <li>Workers generate structured reports after request completion.</li>
  <li>Customers can view reports for their own requests.</li>
  <li>Customers can submit reviews (one per completed request) to support service transparency.</li>
</ul>

<h3>Notification System</h3>
<ul>
  <li>Notifications created for critical events (approval, rejection, start, completion, review creation, etc.).</li>
  <li>Supports customer, company, and worker notification flows.</li>
</ul>

<h3>AI-Powered Assistance (Subscription Based)</h3>
<ul>
  <li>Cost estimation and breakdowns (customer/company perspective).</li>
  <li>Timeline estimation and planning guidance.</li>
  <li>Issue diagnosis (text &amp; image URL input).</li>
  <li>Inspection planning checklists and prioritization.</li>
  <li>Worker safety requirements and repair checklists.</li>
  <li>Redesign scope &amp; style suggestions.</li>
</ul>

<h3>Subscription &amp; Billing</h3>
<ul>
  <li><strong>FREE</strong> and <strong>AI</strong> plans.</li>
  <li>AI features available only for active subscriptions.</li>
  <li>Scheduled email reminders for renewal and expiry.</li>
</ul>

<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="roles-permissions">3. User Roles &amp; Permissions</h2>

<h3>Customer</h3>
<ul>
  <li>Manage profile and properties.</li>
  <li>Create service requests (inspection, moving, maintenance, redesign).</li>
  <li>View/manage own requests and offers.</li>
  <li>Accept/reject offers and pay for services.</li>
  <li>View reports and submit reviews after completion.</li>
  <li>Use AI features if subscribed.</li>
</ul>

<h3>Company (Role-specialized)</h3>
<p>
  Company accounts are specialized by role:
  <strong>INSPECTION_COMPANY</strong>, <strong>MOVING_COMPANY</strong>, <strong>MAINTENANCE_COMPANY</strong>, <strong>REDESIGN_COMPANY</strong>.
</p>
<ul>
  <li>Requires admin approval to operate.</li>
  <li>View assigned requests.</li>
  <li>Approve/reject pending requests and create pricing offers.</li>
  <li>Start requests only after validating: offer accepted + request paid + correct state.</li>
  <li>Assign/release resources (workers; vehicles for moving).</li>
  <li>View reviews and notifications related to company operations.</li>
</ul>

<h3>Worker</h3>
<ul>
  <li>Works under a company and is assigned to requests.</li>
  <li>Can access only requests assigned to them.</li>
  <li>Create/update/delete reports (after request completion).</li>
  <li>AI tools available only if: subscription is active + worker account is active.</li>
</ul>

<h3>Admin</h3>
<ul>
  <li>Approve/reject company registrations.</li>
  <li>Oversee platform data, users, and notifications.</li>
  <li>Manage platform-level operations and governance flows.</li>
</ul>

<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="architecture">4. Architecture</h2>
<p>
  HomeWay follows a layered Spring Boot architecture to keep responsibilities clean and scalable:
</p>

<h3>Controller Layer</h3>
<ul>
  <li>RESTful APIs for customers, companies, workers, and admin.</li>
  <li>Authentication via Spring Security (Basic Auth) and <code>@AuthenticationPrincipal</code>.</li>
</ul>

<h3>Service Layer</h3>
<ul>
  <li>Business logic + workflow enforcement (state transitions, ownership checks, role gating).</li>
  <li>Transactional operations for request lifecycle, resource assignment, and payment confirmation.</li>
  <li>Subscription checks that gate AI features.</li>
</ul>

<h3>Persistence Layer</h3>
<ul>
  <li>JPA/Hibernate relational mapping.</li>
  <li>Strong entity ownership via <code>@OneToOne</code>, <code>@ManyToOne</code>, and <code>@MapsId</code>.</li>
</ul>

<h3>External Integrations</h3>
<ul>
  <li><strong>Moyasar</strong> for payments (offers + subscription billing).</li>
  <li><strong>OpenAI API</strong> for AI assistance endpoints.</li>
  <li>Email service for renewal/expiry notifications.</li>
</ul>

<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="core-workflows">5. Core Workflows</h2>

<h3>Service Request Flow</h3>
<ol>
  <li>Customer selects a property and a company, then creates a request (<strong>Pending</strong>).</li>
  <li>Company reviews the request:
    <ul>
      <li><strong>Approve</strong>: creates an offer with a price → request becomes <strong>Approved</strong>.</li>
      <li><strong>Reject</strong>: request becomes <strong>Rejected</strong>.</li>
    </ul>
  </li>
  <li>Customer accepts the offer and completes payment (gates service execution).</li>
  <li>Company starts the request:
    <ul>
      <li>Validates offer is accepted + request is paid + correct status.</li>
      <li>Assigns an available worker (and a vehicle for moving requests).</li>
      <li>Updates availability and marks request <strong>In Progress</strong>.</li>
    </ul>
  </li>
  <li>Company completes the request:
    <ul>
      <li>Releases worker/vehicle resources and restores availability.</li>
      <li>Marks request <strong>Completed</strong> and stores completion dates.</li>
    </ul>
  </li>
  <li>Notifications are created at key steps for customer/company/worker visibility.</li>
</ol>

<h3>Payment Flow</h3>
<ol>
  <li>Customer pays an accepted offer via Moyasar.</li>
  <li>System verifies payment status using Moyasar APIs.</li>
  <li>Request <code>isPaid</code> is updated automatically upon successful verification.</li>
  <li>Request execution (start/complete) is blocked unless paid.</li>
</ol>

<h3>AI Feature Flow</h3>
<ol>
  <li>User subscribes to AI plan (subscription stored and payment tracked).</li>
  <li>Every AI endpoint validates subscription status before responding.</li>
  <li>Additional gating rules apply depending on endpoint:
    <ul>
      <li>Worker must be active for worker tools.</li>
      <li>Company role must match feature type (e.g., moving tools for moving company).</li>
    </ul>
  </li>
  <li>AI responses are returned in structured, actionable formats.</li>
</ol>

<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="problems-solved">6. Problems Solved</h2>
<ul>
  <li><strong>Unstructured service requests:</strong> Enforces a controlled lifecycle and valid state transitions.</li>
  <li><strong>Pricing ambiguity:</strong> Uses explicit offers + acceptance before payment.</li>
  <li><strong>Resource conflicts:</strong> Prevents double-assignment of workers/vehicles with availability tracking.</li>
  <li><strong>Unauthorized access:</strong> Strong role-based and ownership validation for sensitive operations.</li>
  <li><strong>Poor communication:</strong> Built-in notifications for all critical service events.</li>
  <li><strong>Lack of decision support:</strong> AI tools assist planning, estimation, diagnosis, and documentation.</li>
  <li><strong>Scalability:</strong> Modular structure supports adding service types and AI tools without redesign.</li>
</ul>

<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="contributing">7. Contributing</h2>
<p>
  This project was developed as a collaborative capstone project. The current version represents a complete implementation 
  of the HomeWay platform with all planned features and workflows.
</p>
<p>
  While this repository showcases our final work, we welcome feedback, suggestions, and discussions about the 
  implementation. If you're interested in learning more about specific features or have questions about our approach:
</p>
<ul>
  <li>Open an issue to discuss potential improvements or ask questions about the architecture.</li>
  <li>Review our comprehensive documentation to understand the design decisions and workflows.</li>
  <li>Check out the Postman documentation and test cases to explore the API endpoints.</li>
</ul>
<p>
  If you'd like to build upon this work or adapt it for your own use case, feel free to fork the repository. 
  Please ensure you maintain the existing structure (Controller → Service → Repository) and preserve validation 
  and authorization rules when making modifications.
</p>

<p><a href="#top">↑ Back to top</a></p>

<hr />

<h2 id="contributors">8. Contributors</h2>

<h3><a href="https://github.com/Turki1927">@Turki1927</a></h3>
<p><strong>Backend Development:</strong></p>
<ul>
  <li>Property, Worker, Admin entities</li>
  <li>Notifications system</li>
  <li>SubscriptionPaymentService, UserSubscriptionService</li>
  <li>Request flows: Maintenance &amp; Redesign</li>
</ul>
<p><strong>Testing &amp; Design:</strong></p>
<ul>
  <li>JUnit Test (Service layer)</li>
  <li>Postman Testing (Local/deployment)</li>
  <li>Figma Initial Design</li>
  <li>Class Diagram</li>
</ul>
<p><strong>External APIs:</strong></p>
<ul>
  <li>Subscription payment integration (Moyasar)</li>
  <li>Email Integration (Subscription/Notifications)</li>
  <li>AI endpoints: customerServicesTimeEstimation, customerReviewWritingAssist, workerRepairChecklist, workerSafetyRequirements, companyServiceEstimationCost, maintenanceCompanySparePartsCosts</li>
</ul>

<h3><a href="https://github.com/leenref">@leenref</a></h3>
<p><strong>Backend Development:</strong></p>
<ul>
  <li>Report, Vehicle, Customer entities</li>
  <li>RequestPaymentService</li>
  <li>Request flow: Moving</li>
</ul>
<p><strong>Testing &amp; Documentation:</strong></p>
<ul>
  <li>JUnit Test (Controller layer)</li>
  <li>Postman Testing (Local/deployment)</li>
  <li>Postman Documentation</li>
  <li>Figma Initial Design</li>
  <li>Class Diagram</li>
</ul>
<p><strong>Deployment &amp; External APIs:</strong></p>
<ul>
  <li>Platform Deployment</li>
  <li>Service payment integration (Moyasar)</li>
  <li>AI endpoints: customerAskAIWhatServiceDoesTheIssueFits, customerIsFixOrDesignCheaper, workerReportCreationAssistant, companyInspectionPlanningAssistant, movingCompanyTimeAdvice, maintenanceFixOrReplace</li>
</ul>

<h3><a href="https://github.com/OsamaAlahmadi-90">@OsamaAlahmadi-90</a></h3>
<p><strong>Backend Development:</strong></p>
<ul>
  <li>User, Offer, UserRegister, Review, Company entities</li>
  <li>Request flow: Inspection</li>
</ul>
<p><strong>Testing &amp; Design:</strong></p>
<ul>
  <li>JUnit Test (Repository layer)</li>
  <li>Postman Testing (Local/deployment)</li>
  <li>Figma Final Design</li>
  <li>Class Diagram</li>
  <li>Use Case Diagram</li>
  <li>Project Presentation</li>
</ul>
<p><strong>External APIs:</strong></p>
<ul>
  <li>Email integration (Company/Customer Requests)</li>
  <li>AI endpoints: customerRequestCostEstimation, customerReportSummary, workerIssueDiagnosis, movingCompanyResourceMovingEstimation, workerJobTimeEstimation, maintenanceCompanyMaintenancePlan, redesignCompanyRedesignScope, customerRedesignFromImage, companyIssueImageDiagnosis</li>
</ul>

<p><a href="#top">↑ Back to top</a></p>

</div>

<!-- Arabic Content -->
<div id="content-ar" class="lang-content rtl">

<h1 id="top-ar">HomeWay – منصة إدارة خدمات الممتلكات</h1>

<p>
  <strong>HomeWay</strong> هي منصة خلفية مبنية بـ Spring Boot تربط العملاء بشركات الخدمات المتخصصة لإدارة العقارات السكنية بكفاءة.
  تدعم دورة حياة الخدمة الكاملة لـ <strong>الفحص</strong>، <strong>الصيانة</strong>، <strong>النقل</strong>، و<strong>إعادة التصميم</strong>،
  مع تطبيق سير عمل منظم، والتحكم الآمن في الوصول، والتسعير الشفاف، والإشعارات الفورية، ومعالجة المدفوعات، والمساعدة المدعومة بالذكاء الاصطناعي.
</p>

<hr />

<h2>جدول المحتويات</h2>
<ul>
  <li><a href="#introduction-ar">1. المقدمة</a></li>
  <li><a href="#key-features-ar">2. الميزات الرئيسية</a></li>
  <li><a href="#roles-permissions-ar">3. أدوار المستخدمين والصلاحيات</a></li>
  <li><a href="#architecture-ar">4. البنية المعمارية</a></li>
  <li><a href="#core-workflows-ar">5. سير العمل الأساسي</a></li>
  <li><a href="#problems-solved-ar">6. المشاكل التي تم حلها</a></li>
  <li><a href="#contributing-ar">7. المساهمة</a></li>
  <li><a href="#contributors-ar">8. المساهمون</a></li>
</ul>

<hr />

<h2 id="introduction-ar">1. المقدمة</h2>
<p>
  تم تصميم HomeWay كنظام خلفي واقعي على مستوى الإنتاج. يدمج قواعد العمل، والترخيص القائم على الأدوار،
  والتحقق من الدفع، والإشعارات، والتقارير، وميزات الذكاء الاصطناعي المقيدة بالاشتراك لتحسين اتخاذ القرار للعملاء
  ومقدمي الخدمات.
</p>
<p>
  تتمحور المنصة حول دورة حياة طلب خدمة محكومة:
  <strong>قيد الانتظار ← موافق عليه ← قيد التنفيذ ← مكتمل</strong> (أو <strong>مرفوض</strong>)،
  مع التحقق الصارم من صلاحيات الأدوار، والملكية، وانتقالات الحالة.
</p>
<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="key-features-ar">2. الميزات الرئيسية</h2>

<h3>إدارة الممتلكات</h3>
<ul>
  <li>يمكن للعملاء إنشاء وتحديث وإدارة عدة عقارات.</li>
  <li>جميع طلبات الخدمة مرتبطة بعقار محدد لتتبع أفضل.</li>
</ul>

<h3>دورة حياة طلب الخدمة</h3>
<ul>
  <li>سير عمل محكوم: <strong>قيد الانتظار ← موافق عليه ← قيد التنفيذ ← مكتمل</strong> (أو <strong>مرفوض</strong>).</li>
  <li>الطلبات مرتبطة صراحةً بشركة وعقار.</li>
  <li>التحقق الصارم من انتقالات حالة الطلب وفحوصات الملكية.</li>
</ul>

<h3>نظام العروض والدفع</h3>
<ul>
  <li>الشركات توافق على الطلبات من خلال إنشاء عرض سعر.</li>
  <li>العملاء يمكنهم قبول أو رفض العروض قبل الدفع.</li>
  <li>معالجة دفع آمنة عبر <strong>ميسر (Moyasar)</strong> مع التحقق والتحديث التلقائي لحالة الدفع.</li>
</ul>

<h3>إدارة الموارد</h3>
<ul>
  <li>تعيين تلقائي للعمال المتاحين للطلبات.</li>
  <li>تعيين المركبات لخدمات النقل.</li>
  <li>تتبع التوافر لمنع الحجوزات المزدوجة والتعارضات.</li>
</ul>

<h3>التقارير والمراجعات</h3>
<ul>
  <li>العمال ينشئون تقارير منظمة بعد إكمال الطلب.</li>
  <li>العملاء يمكنهم عرض التقارير لطلباتهم الخاصة.</li>
  <li>العملاء يمكنهم تقديم مراجعات (مراجعة واحدة لكل طلب مكتمل) لدعم الشفافية.</li>
</ul>

<h3>نظام الإشعارات</h3>
<ul>
  <li>إنشاء إشعارات للأحداث الحرجة (الموافقة، الرفض، البدء، الإكمال، إنشاء المراجعة، إلخ).</li>
  <li>يدعم تدفقات الإشعارات للعملاء والشركات والعمال.</li>
</ul>

<h3>المساعدة المدعومة بالذكاء الاصطناعي (على أساس الاشتراك)</h3>
<ul>
  <li>تقدير التكلفة والتفاصيل (من منظور العميل/الشركة).</li>
  <li>تقدير الجدول الزمني وإرشادات التخطيط.</li>
  <li>تشخيص المشاكل (نص ورابط صورة).</li>
  <li>قوائم مراجعة تخطيط الفحص وتحديد الأولويات.</li>
  <li>متطلبات السلامة للعمال وقوائم مراجعة الإصلاح.</li>
  <li>نطاق إعادة التصميم واقتراحات الأسلوب.</li>
</ul>

<h3>الاشتراك والفوترة</h3>
<ul>
  <li>خطط <strong>مجانية</strong> و<strong>الذكاء الاصطناعي</strong>.</li>
  <li>ميزات الذكاء الاصطناعي متاحة فقط للاشتراكات النشطة.</li>
  <li>تذكيرات مجدولة عبر البريد الإلكتروني للتجديد والانتهاء.</li>
</ul>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="roles-permissions-ar">3. أدوار المستخدمين والصلاحيات</h2>

<h3>العميل</h3>
<ul>
  <li>إدارة الملف الشخصي والعقارات.</li>
  <li>إنشاء طلبات الخدمة (فحص، نقل، صيانة، إعادة تصميم).</li>
  <li>عرض/إدارة الطلبات والعروض الخاصة.</li>
  <li>قبول/رفض العروض والدفع مقابل الخدمات.</li>
  <li>عرض التقارير وتقديم المراجعات بعد الإكمال.</li>
  <li>استخدام ميزات الذكاء الاصطناعي إذا كان مشتركًا.</li>
</ul>

<h3>الشركة (متخصصة حسب الدور)</h3>
<p>
  حسابات الشركات متخصصة حسب الدور:
  <strong>شركة فحص</strong>، <strong>شركة نقل</strong>، <strong>شركة صيانة</strong>، <strong>شركة إعادة تصميم</strong>.
</p>
<ul>
  <li>تتطلب موافقة المسؤول للعمل.</li>
  <li>عرض الطلبات المخصصة.</li>
  <li>الموافقة/رفض الطلبات المعلقة وإنشاء عروض الأسعار.</li>
  <li>بدء الطلبات فقط بعد التحقق من: قبول العرض + دفع الطلب + الحالة الصحيحة.</li>
  <li>تعيين/إطلاق الموارد (العمال؛ المركبات للنقل).</li>
  <li>عرض المراجعات والإشعارات المتعلقة بعمليات الشركة.</li>
</ul>

<h3>العامل</h3>
<ul>
  <li>يعمل تحت شركة ويُعين للطلبات.</li>
  <li>يمكنه الوصول فقط للطلبات المعينة له.</li>
  <li>إنشاء/تحديث/حذف التقارير (بعد إكمال الطلب).</li>
  <li>أدوات الذكاء الاصطناعي متاحة فقط إذا: الاشتراك نشط + حساب العامل نشط.</li>
</ul>

<h3>المسؤول</h3>
<ul>
  <li>الموافقة/رفض تسجيلات الشركات.</li>
  <li>الإشراف على بيانات المنصة والمستخدمين والإشعارات.</li>
  <li>إدارة العمليات والحوكمة على مستوى المنصة.</li>
</ul>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="architecture-ar">4. البنية المعمارية</h2>
<p>
  تتبع HomeWay بنية Spring Boot متعددة الطبقات للحفاظ على المسؤوليات واضحة وقابلة للتوسع:
</p>

<h3>طبقة المتحكم (Controller)</h3>
<ul>
  <li>واجهات برمجية RESTful للعملاء والشركات والعمال والمسؤولين.</li>
  <li>المصادقة عبر Spring Security (Basic Auth) و<code>@AuthenticationPrincipal</code>.</li>
</ul>

<h3>طبقة الخدمة (Service)</h3>
<ul>
  <li>منطق الأعمال + تطبيق سير العمل (انتقالات الحالة، فحوصات الملكية، تقييد الأدوار).</li>
  <li>العمليات المعاملية لدورة حياة الطلب، وتعيين الموارد، وتأكيد الدفع.</li>
  <li>فحوصات الاشتراك التي تقيد ميزات الذكاء الاصطناعي.</li>
</ul>

<h3>طبقة الاستمرارية (Persistence)</h3>
<ul>
  <li>التعيين العلائقي JPA/Hibernate.</li>
  <li>ملكية قوية للكيانات عبر <code>@OneToOne</code>، <code>@ManyToOne</code>، و<code>@MapsId</code>.</li>
</ul>

<h3>التكاملات الخارجية</h3>
<ul>
  <li><strong>ميسر (Moyasar)</strong> للمدفوعات (العروض + فوترة الاشتراك).</li>
  <li><strong>OpenAI API</strong> لنقاط نهاية مساعدة الذكاء الاصطناعي.</li>
  <li>خدمة البريد الإلكتروني لإشعارات التجديد والانتهاء.</li>
</ul>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="core-workflows-ar">5. سير العمل الأساسي</h2>

<h3>سير طلب الخدمة</h3>
<ol>
  <li>العميل يختار عقارًا وشركة، ثم ينشئ طلبًا (<strong>قيد الانتظار</strong>).</li>
  <li>الشركة تراجع الطلب:
    <ul>
      <li><strong>الموافقة</strong>: تنشئ عرضًا بسعر ← الطلب يصبح <strong>موافق عليه</strong>.</li>
      <li><strong>الرفض</strong>: الطلب يصبح <strong>مرفوضًا</strong>.</li>
    </ul>
  </li>
  <li>العميل يقبل العرض ويكمل الدفع (بوابة تنفيذ الخدمة).</li>
  <li>الشركة تبدأ الطلب:
    <ul>
      <li>التحقق من: قبول العرض + دفع الطلب + الحالة الصحيحة.</li>
      <li>تعيين عامل متاح (ومركبة لطلبات النقل).</li>
      <li>تحديث التوافر ووضع علامة الطلب <strong>قيد التنفيذ</strong>.</li>
    </ul>
  </li>
  <li>الشركة تكمل الطلب:
    <ul>
      <li>إطلاق موارد العامل/المركبة واستعادة التوافر.</li>
      <li>وضع علامة الطلب <strong>مكتمل</strong> وتخزين تواريخ الإكمال.</li>
    </ul>
  </li>
  <li>يتم إنشاء الإشعارات في الخطوات الرئيسية لرؤية العميل/الشركة/العامل.</li>
</ol>

<h3>سير الدفع</h3>
<ol>
  <li>العميل يدفع عرضًا مقبولاً عبر ميسر.</li>
  <li>النظام يتحقق من حالة الدفع باستخدام واجهات برمجة ميسر.</li>
  <li>يتم تحديث <code>isPaid</code> للطلب تلقائيًا عند نجاح التحقق.</li>
  <li>تنفيذ الطلب (البدء/الإكمال) محظور ما لم يتم الدفع.</li>
</ol>

<h3>سير ميزة الذكاء الاصطناعي</h3>
<ol>
  <li>المستخدم يشترك في خطة الذكاء الاصطناعي (يتم تخزين الاشتراك وتتبع الدفع).</li>
  <li>كل نقطة نهاية للذكاء الاصطناعي تتحقق من حالة الاشتراك قبل الاستجابة.</li>
  <li>قواعد تقييد إضافية تنطبق حسب نقطة النهاية:
    <ul>
      <li>يجب أن يكون العامل نشطًا لأدوات العامل.</li>
      <li>يجب أن يتطابق دور الشركة مع نوع الميزة (مثلاً، أدوات النقل لشركة النقل).</li>
    </ul>
  </li>
  <li>يتم إرجاع استجابات الذكاء الاصطناعي بتنسيقات منظمة وقابلة للتنفيذ.</li>
</ol>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="problems-solved-ar">6. المشاكل التي تم حلها</h2>
<ul>
  <li><strong>طلبات خدمة غير منظمة:</strong> تطبيق دورة حياة محكومة وانتقالات حالة صالحة.</li>
  <li><strong>غموض التسعير:</strong> استخدام عروض صريحة + قبول قبل الدفع.</li>
  <li><strong>تعارضات الموارد:</strong> منع التعيين المزدوج للعمال/المركبات مع تتبع التوافر.</li>
  <li><strong>الوصول غير المصرح به:</strong> التحقق القوي القائم على الدور والملكية للعمليات الحساسة.</li>
  <li><strong>ضعف التواصل:</strong> إشعارات مدمجة لجميع أحداث الخدمة الحرجة.</li>
  <li><strong>نقص دعم القرار:</strong> أدوات الذكاء الاصطناعي تساعد في التخطيط والتقدير والتشخيص والتوثيق.</li>
  <li><strong>قابلية التوسع:</strong> بنية معيارية تدعم إضافة أنواع خدمات وأدوات ذكاء اصطناعي دون إعادة تصميم.</li>
</ul>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="contributing-ar">7. المساهمة</h2>
<p>
  تم تطوير هذا المشروع كمشروع تخرج تعاوني. النسخة الحالية تمثل تنفيذًا كاملاً
  لمنصة HomeWay مع جميع الميزات وسير العمل المخططة.
</p>
<p>
  بينما يعرض هذا المستودع عملنا النهائي، نرحب بالملاحظات والاقتراحات والمناقشات حول
  التنفيذ. إذا كنت مهتمًا بمعرفة المزيد عن ميزات محددة أو لديك أسئلة حول منهجنا:
</p>
<ul>
  <li>افتح مشكلة (issue) لمناقشة التحسينات المحتملة أو طرح أسئلة حول البنية المعمارية.</li>
  <li>راجع وثائقنا الشاملة لفهم قرارات التصميم وسير العمل.</li>
  <li>تحقق من وثائق Postman وحالات الاختبار لاستكشاف نقاط نهاية الواجهة البرمجية.</li>
</ul>
<p>
  إذا كنت ترغب في البناء على هذا العمل أو تكييفه لحالة الاستخدام الخاصة بك، فلا تتردد في عمل fork للمستودع.
  يرجى التأكد من الحفاظ على البنية الحالية (Controller → Service → Repository) والحفاظ على قواعد التحقق
  والترخيص عند إجراء التعديلات.
</p>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

<hr />

<h2 id="contributors-ar">8. المساهمون</h2>

<h3><a href="https://github.com/Turki1927">@Turki1927</a></h3>
<p><strong>تطوير الواجهة الخلفية:</strong></p>
<ul>
  <li>كيانات العقار، العامل، المسؤول</li>
  <li>نظام الإشعارات</li>
  <li>SubscriptionPaymentService، UserSubscriptionService</li>
  <li>سير طلبات: الصيانة وإعادة التصميم</li>
</ul>
<p><strong>الاختبار والتصميم:</strong></p>
<ul>
  <li>اختبار JUnit (طبقة الخدمة)</li>
  <li>اختبار Postman (محلي/نشر)</li>
  <li>التصميم الأولي على Figma</li>
  <li>مخطط الفئات (Class Diagram)</li>
</ul>
<p><strong>الواجهات البرمجية الخارجية:</strong></p>
<ul>
  <li>تكامل دفع الاشتراك (ميسر)</li>
  <li>تكامل البريد الإلكتروني (الاشتراك/الإشعارات)</li>
  <li>نقاط نهاية الذكاء الاصطناعي: customerServicesTimeEstimation، customerReviewWritingAssist، workerRepairChecklist، workerSafetyRequirements، companyServiceEstimationCost، maintenanceCompanySparePartsCosts</li>
</ul>

<h3><a href="https://github.com/leenref">@leenref</a></h3>
<p><strong>تطوير الواجهة الخلفية:</strong></p>
<ul>
  <li>كيانات التقرير، المركبة، العميل</li>
  <li>RequestPaymentService</li>
  <li>سير طلب: النقل</li>
</ul>
<p><strong>الاختبار والوثائق:</strong></p>
<ul>
  <li>اختبار JUnit (طبقة المتحكم)</li>
  <li>اختبار Postman (محلي/نشر)</li>
  <li>وثائق Postman</li>
  <li>التصميم الأولي على Figma</li>
  <li>مخطط الفئات (Class Diagram)</li>
</ul>
<p><strong>النشر والواجهات البرمجية الخارجية:</strong></p>
<ul>
  <li>نشر المنصة</li>
  <li>تكامل دفع الخدمة (ميسر)</li>
  <li>نقاط نهاية الذكاء الاصطناعي: customerAskAIWhatServiceDoesTheIssueFits، customerIsFixOrDesignCheaper، workerReportCreationAssistant، companyInspectionPlanningAssistant، movingCompanyTimeAdvice، maintenanceFixOrReplace</li>
</ul>

<h3><a href="https://github.com/OsamaAlahmadi-90">@OsamaAlahmadi-90</a></h3>
<p><strong>تطوير الواجهة الخلفية:</strong></p>
<ul>
  <li>كيانات المستخدم، العرض، UserRegister، المراجعة، الشركة</li>
  <li>سير طلب: الفحص</li>
</ul>
<p><strong>الاختبار والتصميم:</strong></p>
<ul>
  <li>اختبار JUnit (طبقة المستودع)</li>
  <li>اختبار Postman (محلي/نشر)</li>
  <li>التصميم النهائي على Figma</li>
  <li>مخطط الفئات (Class Diagram)</li>
  <li>مخطط حالة الاستخدام (Use Case Diagram)</li>
  <li>العرض التقديمي للمشروع</li>
</ul>
<p><strong>الواجهات البرمجية الخارجية:</strong></p>
<ul>
  <li>تكامل البريد الإلكتروني (طلبات الشركة/العميل)</li>
  <li>نقاط نهاية الذكاء الاصطناعي: customerRequestCostEstimation، customerReportSummary، workerIssueDiagnosis، movingCompanyResourceMovingEstimation، workerJobTimeEstimation، maintenanceCompanyMaintenancePlan، redesignCompanyRedesignScope، customerRedesignFromImage، companyIssueImageDiagnosis</li>
</ul>

<p><a href="#top-ar">↑ العودة للأعلى</a></p>

</div>

<script>
function switchLanguage(lang) {
    // Hide all content
    document.getElementById('content-en').classList.remove('active');
    document.getElementById('content-ar').classList.remove('active');
    
    // Remove active from all buttons
    document.getElementById('btn-en').classList.remove('active');
    document.getElementById('btn-ar').classList.remove('active');
    
    // Show selected content and activate button
    if (lang === 'en') {
        document.getElementById('content-en').classList.add('active');
        document.getElementById('btn-en').classList.add('active');
    } else {
        document.getElementById('content-ar').classList.add('active');
        document.getElementById('btn-ar').classList.add('active');
    }
}
</script>

</body>
</html>
