function metricGonogo() {
    return {
        restrict: 'E',
        templateUrl: 'views/partial/gonogo.html',
        scope: {
            metric: "@"
        },
        controller: function ($scope, $http) {
            $http({method: 'GET', url: '/app/metrics/'+ $scope.metric})
                .success(function(data) {
                    $scope.data = data;
                    switch(data.status) {
                        case "green":
                            $scope.message = "a GO";
                            $scope.icon = "fa-check-circle"
                            $scope.color = "text-navy"
                            break;
                        case "red":
                            $scope.message = "NO GO";
                            $scope.icon = "fa-exclamation-circle"
                            $scope.color = "text-danger"
                            break;
                        case "building":
                            $scope.message = "building..."
                            $scope.icon = "fa-spinner"
                            $scope.color = "text-muted"
                            break;
                        default:
                            $scope.message = "unknown"
                            $scope.icon = "fa-question-circle"
                            $scope.color = "text-warning"
                    }
                });

        }
    };
}

kuona.directive('metricGonogo', metricGonogo);
