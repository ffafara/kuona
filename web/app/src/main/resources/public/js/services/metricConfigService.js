angular.module('kuona.dashboard').factory('MetricConfig', ['$resource', function MetricConfigFactory($resource) {
  return $resource('/app/metrics/configs/:name', {}, {});
}]);
