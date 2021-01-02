from rest_framework.serializers import ModelSerializer
from .models import Review

from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter

class ReviewSerializer(ModelSerializer):
    class Meta:
        model = Review
        fields = '__all__'