from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from .serializers import ReviewSerializer
from rest_framework import status
from .models import Review

from rest_framework.viewsets import ModelViewSet
from rest_framework.filters import SearchFilter

# Create your views here.

class ReviewQuerySet(ModelViewSet):
    queryset = Review.objects.all()
    serializer_class = ReviewSerializer

    def get_review(self, statId):
        queryset = super().get_queryset()
        queryset = queryset.filter(statId=statId)
        return queryset

class ReviewView(APIView):
    def post(self, request):
        review_serializer = ReviewSerializer(data=request.data)
        if review_serializer.is_valid():
            review_serializer.save()
            return Response(review_serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response(review_serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def get(self, request):
        statId = request.GET.get('statId')
        review_queryset = ReviewQuerySet().get_review(statId)

        review_queryset_serializer = ReviewSerializer(review_queryset, many=True)
        return Response(review_queryset_serializer.data, status=status.HTTP_200_OK)
