/**
 * INSPINIA - Responsive Admin Theme
 *
 */


/**
 * MainCtrl - controller
 */
function MainController($scope) {
    $scope.main.userName = 'Test User';
    //this.userName = 'Example user';
    this.helloText = 'Welcome in SeedProject';
    this.descriptionText = 'It is an application skeleton for a typical AngularJS web app. You can use it to quickly bootstrap your angular webapp projects and dev environment for these projects.';

};

kuona.controller('MainController', MainController);

kuona.controller('ProjectListController', ['$scope', '$http', function ($scope, $http) {
    $http.get('/app/projects')
        .then(function (res) {
            $scope.projects = res.data;
        });
}]);

function ProjectEditController($scope, $http, $state) {
    $scope.cancelEdit = function() {
            $state.go('index.dashboard');
        }
}

kuona.controller('ProjectEditController', ProjectEditController);

function MetricConfigController($scope, $http, $state, MetricConfig) {
    $scope.metricTypes = ["GoNoGo", "Other-Metric"];
    $scope.buildServers = ["snap", "gocd"];

    $scope.metricConfig = new MetricConfig();

    $scope.cancelEdit = function() {
            $state.go('index.dashboard');
        }

    $scope.submit = function(metricConfig) {
        metricConfig['config'] = JSON.parse(metricConfig['config']);
        console.log(metricConfig);
        metricConfig.$save();
//TODO:        add error handling
        $state.go('index.dashboard');
    }
};

kuona.controller('MetricConfigController', MetricConfigController);