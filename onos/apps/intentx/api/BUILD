COMPILE_DEPS = CORE_DEPS + JACKSON + REST + [
    "//core/common:onos-core-common",
    "//apps/intentx/app:onos-apps-intentx-app",
]

osgi_jar_with_tests(
    api_description = "REST API v1.0, for Extended Intent",
    api_package = "com.ict.intentx.rest",
    api_title = "Intent REST API Extension",
    api_version = "1.0",
    test_deps = TEST_REST,
    web_context = "/onos/v1/intentx",
    deps = COMPILE_DEPS,
)
