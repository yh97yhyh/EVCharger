from django.urls import path
from . import views

urlpatterns = [
    path('', views.StationsView.as_view())
]